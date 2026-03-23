import * as path from 'path';
import * as fs from 'fs';
import { workspace, FileSystemWatcher, Disposable, Event, EventEmitter, window, WorkspaceFolder } from 'vscode';
import { IAppConfig, CONFIG_FILE_NAME, DEFAULT_IAPP_CONFIG } from './IAppConfig';
import { ConfigValidator, ValidationResult } from './ConfigValidator';

export interface ConfigChangeEvent {
    oldConfig: IAppConfig | null;
    newConfig: IAppConfig;
    validation: ValidationResult;
    workspaceFolder: WorkspaceFolder;
}

export class ConfigManager implements Disposable {
    private configs: Map<string, IAppConfig> = new Map();
    private validations: Map<string, ValidationResult> = new Map();
    private watcher: FileSystemWatcher | null = null;
    private readonly _onConfigChange = new EventEmitter<ConfigChangeEvent>();
    
    readonly onConfigChange: Event<ConfigChangeEvent> = this._onConfigChange.event;

    constructor() {
        this.initialize();
    }

    private initialize(): void {
        this.watcher = workspace.createFileSystemWatcher(`**/${CONFIG_FILE_NAME}`);
        
        this.watcher.onDidCreate(uri => this.handleConfigChange(uri));
        this.watcher.onDidChange(uri => this.handleConfigChange(uri));
        this.watcher.onDidDelete(uri => this.handleConfigDelete(uri));

        if (workspace.workspaceFolders) {
            for (const folder of workspace.workspaceFolders) {
                this.loadConfig(folder);
            }
        }

        workspace.onDidChangeWorkspaceFolders(e => {
            for (const folder of e.removed) {
                this.configs.delete(folder.uri.fsPath);
                this.validations.delete(folder.uri.fsPath);
            }
            for (const folder of e.added) {
                this.loadConfig(folder);
            }
        });
    }

    private handleConfigChange(uri: import('vscode').Uri): void {
        const folder = workspace.getWorkspaceFolder(uri);
        if (folder) {
            const oldConfig = this.configs.get(folder.uri.fsPath) || null;
            this.loadConfig(folder, oldConfig);
        }
    }

    private handleConfigDelete(uri: import('vscode').Uri): void {
        const folder = workspace.getWorkspaceFolder(uri);
        if (folder) {
            const oldConfig = this.configs.get(folder.uri.fsPath) || null;
            this.configs.delete(folder.uri.fsPath);
            this.validations.delete(folder.uri.fsPath);
            
            this._onConfigChange.fire({
                oldConfig,
                newConfig: { ...DEFAULT_IAPP_CONFIG },
                validation: { valid: true, errors: [], warnings: ['配置文件已删除，使用默认配置'] },
                workspaceFolder: folder
            });
        }
    }

    private loadConfig(folder: WorkspaceFolder, oldConfig: IAppConfig | null = null): void {
        const configPath = path.join(folder.uri.fsPath, CONFIG_FILE_NAME);
        let config: IAppConfig;
        let validation: ValidationResult;

        if (fs.existsSync(configPath)) {
            try {
                const content = fs.readFileSync(configPath, 'utf-8');
                const parsed = JSON.parse(content);
                validation = ConfigValidator.validate(parsed, folder.uri.fsPath);
                
                if (validation.valid) {
                    config = ConfigValidator.mergeWithDefaults(parsed);
                } else {
                    config = { ...DEFAULT_IAPP_CONFIG };
                    window.showErrorMessage(`配置文件验证失败: ${validation.errors.join(', ')}`);
                }
            } catch (e) {
                config = { ...DEFAULT_IAPP_CONFIG };
                validation = {
                    valid: false,
                    errors: [`配置文件解析失败: ${e instanceof Error ? e.message : String(e)}`],
                    warnings: []
                };
                window.showErrorMessage(`配置文件解析失败: ${configPath}`);
            }
        } else {
            config = { ...DEFAULT_IAPP_CONFIG };
            validation = { valid: true, errors: [], warnings: ['配置文件不存在，使用默认配置'] };
        }

        const previousConfig = oldConfig || this.configs.get(folder.uri.fsPath) || null;
        this.configs.set(folder.uri.fsPath, config);
        this.validations.set(folder.uri.fsPath, validation);

        if (!previousConfig || ConfigValidator.hasYuWebChanged(previousConfig, config)) {
            this._onConfigChange.fire({
                oldConfig: previousConfig,
                newConfig: config,
                validation,
                workspaceFolder: folder
            });
        }
    }

    getConfig(folder: WorkspaceFolder): IAppConfig {
        return this.configs.get(folder.uri.fsPath) || { ...DEFAULT_IAPP_CONFIG };
    }

    getValidation(folder: WorkspaceFolder): ValidationResult {
        return this.validations.get(folder.uri.fsPath) || { valid: true, errors: [], warnings: [] };
    }

    getConfigPath(folder: WorkspaceFolder): string {
        return path.join(folder.uri.fsPath, CONFIG_FILE_NAME);
    }

    hasConfigFile(folder: WorkspaceFolder): boolean {
        return fs.existsSync(this.getConfigPath(folder));
    }

    async createConfigFile(folder: WorkspaceFolder, enableYuWeb: boolean = false): Promise<boolean> {
        const configPath = this.getConfigPath(folder);
        const config: IAppConfig = {
            version: "1.0",
            runDirectory: ".",
            enableYuWeb,
            yuweb: {
                port: 8080,
                debugMode: false,
                safeMode: false,
                preloadScripts: false,
                serveStaticFiles: true
            },
            javaArgs: [],
            jvmArgs: []
        };

        try {
            await workspace.fs.writeFile(
                folder.uri.with({ path: configPath }),
                Buffer.from(JSON.stringify(config, null, 2), 'utf-8')
            );
            return true;
        } catch (e) {
            window.showErrorMessage(`创建配置文件失败: ${e instanceof Error ? e.message : String(e)}`);
            return false;
        }
    }

    dispose(): void {
        if (this.watcher) {
            this.watcher.dispose();
        }
        this._onConfigChange.dispose();
        this.configs.clear();
        this.validations.clear();
    }
}
