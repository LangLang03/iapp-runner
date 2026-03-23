import * as path from 'path';
import * as fs from 'fs';
import * as cp from 'child_process';
import { workspace, ExtensionContext, window, OutputChannel, commands, StatusBarAlignment, StatusBarItem, ConfigurationChangeEvent, WorkspaceFolder, Uri, QuickPickItem } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions, Executable, ExecutableOptions } from 'vscode-languageclient/node';
import { ConfigManager } from './config/ConfigManager';
import { RunnerManager } from './runner/RunnerManager';
import { DEFAULT_IAPP_CONFIG } from './config/IAppConfig';

let client: LanguageClient | undefined;
let outputChannel: OutputChannel;
let statusBarItem: StatusBarItem;
let runStatusBarItem: StatusBarItem;
let configManager: ConfigManager;
let runnerManager: RunnerManager;

const JAVA_MIN_VERSION = 17;
const JAVA_DOWNLOAD_URL = 'https://www.oracle.com/java/technologies/downloads/#java17';

export function activate(context: ExtensionContext) {
    outputChannel = window.createOutputChannel('iApp');
    outputChannel.appendLine('iApp 扩展正在激活...');
    
    statusBarItem = window.createStatusBarItem(StatusBarAlignment.Right, 100);
    statusBarItem.text = '$(sync~spin) iApp LSP';
    statusBarItem.tooltip = 'iApp 语言服务器启动中...';
    statusBarItem.show();
    context.subscriptions.push(statusBarItem);

    runStatusBarItem = window.createStatusBarItem(StatusBarAlignment.Right, 99);
    runStatusBarItem.text = '$(play) 运行';
    runStatusBarItem.tooltip = '运行 iApp 项目';
    runStatusBarItem.command = 'iapp.run';
    runStatusBarItem.show();
    context.subscriptions.push(runStatusBarItem);

    configManager = new ConfigManager();
    context.subscriptions.push(configManager);

    configManager.onConfigChange(async (e) => {
        outputChannel.appendLine(`配置已更改: ${e.workspaceFolder.uri.fsPath}`);
        if (e.validation.errors.length > 0) {
            outputChannel.appendLine(`配置错误: ${e.validation.errors.join(', ')}`);
        }
        if (e.validation.warnings.length > 0) {
            outputChannel.appendLine(`配置警告: ${e.validation.warnings.join(', ')}`);
        }
        
        if (e.oldConfig === null || e.oldConfig.enableYuWeb !== e.newConfig.enableYuWeb) {
            outputChannel.appendLine(`YuWeb 模式变更: ${e.oldConfig?.enableYuWeb ?? '未设置'} -> ${e.newConfig.enableYuWeb}`);
            await restartLanguageServer(context);
        }
    });

    const restartCommand = commands.registerCommand('iapp.restartServer', async () => {
        outputChannel.appendLine('收到重启命令');
        await restartLanguageServer(context);
    });

    const showOutputCommand = commands.registerCommand('iapp.showOutput', () => {
        outputChannel.show();
    });

    const runCommand = commands.registerCommand('iapp.run', async () => {
        await runProject(context);
    });

    const stopCommand = commands.registerCommand('iapp.stop', async () => {
        await stopProject();
    });

    const createConfigCommand = commands.registerCommand('iapp.createConfig', async () => {
        await createConfigFile();
    });

    context.subscriptions.push(restartCommand, showOutputCommand, runCommand, stopCommand, createConfigCommand);
    outputChannel.appendLine('命令已注册');

    context.subscriptions.push(
        workspace.onDidChangeConfiguration((e: ConfigurationChangeEvent) => {
            if (e.affectsConfiguration('iapp.java.path') || e.affectsConfiguration('iapp.lsp.jarPath')) {
                outputChannel.appendLine('Java 配置已更改，正在重启语言服务器...');
                restartLanguageServer(context);
            }
        })
    );

    startLanguageServer(context);
}

async function runProject(context: ExtensionContext): Promise<void> {
    const folder = await selectWorkspaceFolder();
    if (!folder) {
        window.showErrorMessage('请先打开一个工作区');
        return;
    }

    const config = configManager.getConfig(folder);
    
    if (!runnerManager) {
        const javaPath = workspace.getConfiguration('iapp').get<string>('java.path', 'java');
        const classpath = await getClasspath(context);
        
        runnerManager = new RunnerManager({
            javaPath,
            classpath,
            outputChannel
        });
        context.subscriptions.push(runnerManager);
    }

    if (runnerManager.isRunning(folder)) {
        const stop = await window.showWarningMessage('项目正在运行中，是否停止?', '停止', '取消');
        if (stop === '停止') {
            await runnerManager.stop(folder);
            updateRunStatusBar(folder, false);
        }
        return;
    }

    let scriptFile: string | undefined;
    if (!config.enableYuWeb) {
        const editor = window.activeTextEditor;
        if (editor && editor.document.languageId === 'iapp') {
            scriptFile = editor.document.uri.fsPath;
        } else {
            const files = await workspace.findFiles('**/*.{iapp,iyu,myu}', '**/node_modules/**', 20);
            if (files.length === 0) {
                window.showErrorMessage('未找到 iApp 脚本文件');
                return;
            }
            
            const items: QuickPickItem[] = files.map(f => ({
                label: path.basename(f.fsPath),
                description: path.relative(folder.uri.fsPath, f.fsPath)
            }));
            
            const selected = await window.showQuickPick(items, {
                placeHolder: '选择要运行的脚本文件'
            });
            
            if (!selected) return;
            
            scriptFile = files.find(f => path.basename(f.fsPath) === selected.label)?.fsPath;
        }
    }

    outputChannel.show(true);
    await runnerManager.run(folder, config, scriptFile);
    updateRunStatusBar(folder, true);
}

async function stopProject(): Promise<void> {
    const folder = await selectWorkspaceFolder();
    if (!folder) return;

    if (runnerManager) {
        await runnerManager.stop(folder);
        updateRunStatusBar(folder, false);
    }
}

async function selectWorkspaceFolder(): Promise<WorkspaceFolder | undefined> {
    const folders = workspace.workspaceFolders;
    if (!folders || folders.length === 0) {
        return undefined;
    }
    if (folders.length === 1) {
        return folders[0];
    }
    
    const items = folders.map(f => ({
        label: f.name,
        description: f.uri.fsPath,
        folder: f
    }));
    
    const selected = await window.showQuickPick(items, {
        placeHolder: '选择工作区文件夹'
    });
    
    return selected?.folder;
}

function updateRunStatusBar(folder: WorkspaceFolder, isRunning: boolean): void {
    if (isRunning) {
        runStatusBarItem.text = '$(debug-stop) 停止';
        runStatusBarItem.tooltip = `停止运行 (${folder.name})`;
        runStatusBarItem.command = 'iapp.stop';
    } else {
        runStatusBarItem.text = '$(play) 运行';
        runStatusBarItem.tooltip = '运行 iApp 项目';
        runStatusBarItem.command = 'iapp.run';
    }
}

async function createConfigFile(): Promise<void> {
    const folder = await selectWorkspaceFolder();
    if (!folder) {
        window.showErrorMessage('请先打开一个工作区');
        return;
    }

    const enableYuWeb = await window.showQuickPick(
        [
            { label: 'iApp 项目', description: '普通 iApp 脚本项目', enableYuWeb: false },
            { label: 'YuWeb 项目', description: 'Web 服务项目', enableYuWeb: true }
        ],
        { placeHolder: '选择项目类型' }
    );

    if (!enableYuWeb) return;

    await configManager.createConfigFile(folder, enableYuWeb.enableYuWeb);
    window.showInformationMessage(`已创建配置文件: ${configManager.getConfigPath(folder)}`);
}

async function restartLanguageServer(context: ExtensionContext) {
    if (client) {
        statusBarItem.text = '$(sync~spin) iApp LSP';
        statusBarItem.tooltip = '正在重启 iApp 语言服务器...';
        
        try {
            await client.stop();
            outputChannel.appendLine('iApp 语言服务器已停止');
        } catch (e) {
            outputChannel.appendLine(`停止服务器时出错: ${e}`);
        }
    }
    
    startLanguageServer(context);
}

async function startLanguageServer(context: ExtensionContext) {
    try {
        const config = workspace.getConfiguration('iapp');
        const javaPath = config.get<string>('java.path', 'java');
        
        const javaVersion = await checkJavaVersion(javaPath);
        if (javaVersion === null) {
            showErrorWithDownloadLink('未找到 Java 运行环境。iApp LSP 需要 Java 17 或更高版本。');
            return;
        }
        
        if (javaVersion < JAVA_MIN_VERSION) {
            showErrorWithDownloadLink(`Java 版本过低 (当前: ${javaVersion})。iApp LSP 需要 Java 17 或更高版本。`);
            return;
        }
        
        outputChannel.appendLine(`检测到 Java 版本: ${javaVersion}`);

        const serverOptions = await createServerOptions(context);
        if (!serverOptions) return;

        let enableYuWeb = false;
        if (workspace.workspaceFolders && workspace.workspaceFolders.length > 0) {
            const folder = workspace.workspaceFolders[0];
            enableYuWeb = configManager.getConfig(folder).enableYuWeb;
        }

        const clientOptions: LanguageClientOptions = {
            documentSelector: [
                { scheme: 'file', language: 'iapp' },
                { scheme: 'untitled', language: 'iapp' }
            ],
            synchronize: {
                fileEvents: workspace.createFileSystemWatcher('**/*.{iyu,myu,mjava,iapp}')
            },
            outputChannel: outputChannel,
            traceOutputChannel: outputChannel,
            initializationOptions: {
                enableYuWeb: enableYuWeb
            }
        };

        client = new LanguageClient(
            'iappLanguageServer',
            'iApp 语言服务器',
            serverOptions,
            clientOptions
        );

        client.start().then(() => {
            statusBarItem.text = '$(check) iApp LSP';
            statusBarItem.tooltip = 'iApp 语言服务器运行中';
            outputChannel.appendLine('iApp 语言服务器启动成功');
        }).catch((error) => {
            statusBarItem.text = '$(error) iApp LSP';
            statusBarItem.tooltip = 'iApp 语言服务器启动失败';
            const errorMsg = `iApp 语言服务器启动失败: ${error}`;
            outputChannel.appendLine(errorMsg);
            window.showErrorMessage(errorMsg);
        });
    } catch (error) {
        statusBarItem.text = '$(error) iApp LSP';
        statusBarItem.tooltip = 'iApp 语言服务器启动失败';
        const errorMsg = `iApp 语言服务器启动失败: ${error}`;
        outputChannel.appendLine(errorMsg);
        window.showErrorMessage(errorMsg);
    }
}

async function checkJavaVersion(javaPath: string): Promise<number | null> {
    return new Promise((resolve) => {
        const process = cp.spawn(javaPath, ['-version'], { shell: true });
        
        let stderr = '';
        let stdout = '';
        
        process.stderr.on('data', (data) => {
            stderr += data.toString();
        });
        
        process.stdout.on('data', (data) => {
            stdout += data.toString();
        });
        
        process.on('close', (code) => {
            if (code !== 0 && stderr.length === 0 && stdout.length === 0) {
                outputChannel.appendLine(`Java 检测失败，退出码: ${code}`);
                resolve(null);
                return;
            }
            
            const output = stderr + stdout;
            const versionMatch = output.match(/version "?(1\.)?(\d+)/);
            
            if (versionMatch) {
                const version = parseInt(versionMatch[2], 10);
                resolve(version);
            } else {
                outputChannel.appendLine(`无法解析 Java 版本: ${output}`);
                resolve(null);
            }
        });
        
        process.on('error', (err) => {
            outputChannel.appendLine(`Java 检测错误: ${err.message}`);
            resolve(null);
        });
    });
}

function showErrorWithDownloadLink(message: string) {
    statusBarItem.text = '$(error) iApp LSP';
    statusBarItem.tooltip = 'Java 环境错误';
    outputChannel.appendLine(message);
    
    window.showErrorMessage(message, '下载 Java 17').then((selection) => {
        if (selection === '下载 Java 17') {
            const uri = Uri.parse(JAVA_DOWNLOAD_URL);
            require('vscode').env.openExternal(uri);
        }
    });
}

async function createServerOptions(context: ExtensionContext): Promise<ServerOptions | null> {
    const config = workspace.getConfiguration('iapp');
    const javaPath = config.get<string>('java.path', 'java');
    const jarPath = config.get<string>('lsp.jarPath', '');
    const debug = config.get<boolean>('lsp.debug', false);

    let actualJarPath: string | undefined;
    
    if (jarPath && fs.existsSync(jarPath)) {
        actualJarPath = jarPath;
        outputChannel.appendLine(`使用自定义 JAR: ${actualJarPath}`);
    } else {
        actualJarPath = findBundledJar(context);
        if (actualJarPath) {
            outputChannel.appendLine(`使用内置 JAR: ${actualJarPath}`);
        }
    }

    if (!actualJarPath) {
        window.showErrorMessage('未找到 iAppLSP JAR 文件。请在设置中配置 iapp.lsp.jarPath 或确保扩展包含内置 JAR。');
        return null;
    }

    let enableYuWeb = false;
    if (workspace.workspaceFolders && workspace.workspaceFolders.length > 0) {
        const folder = workspace.workspaceFolders[0];
        enableYuWeb = configManager.getConfig(folder).enableYuWeb;
    }

    const args: string[] = ['-jar', actualJarPath];
    if (enableYuWeb) {
        args.push('--yuweb');
    }
    if (debug) {
        args.push('--debug');
    }

    outputChannel.appendLine(`Java 路径: ${javaPath}`);
    outputChannel.appendLine(`启动参数: ${args.join(' ')}`);

    const executable: Executable = {
        command: javaPath,
        args: args,
        options: {
            cwd: workspace.workspaceFolders?.[0]?.uri?.fsPath || process.cwd()
        } as ExecutableOptions
    };

    return {
        run: executable,
        debug: executable
    };
}

async function getClasspath(context: ExtensionContext): Promise<string> {
    const config = workspace.getConfiguration('iapp');
    const jarPath = config.get<string>('lsp.jarPath', '');
    
    if (jarPath && fs.existsSync(jarPath)) {
        return jarPath;
    }
    
    const bundledJar = findBundledJar(context);
    if (bundledJar) {
        return bundledJar;
    }
    
    return '';
}

function findBundledJar(context: ExtensionContext): string | undefined {
    const jarDir = path.join(context.extensionPath, 'jars');
    
    if (!fs.existsSync(jarDir)) {
        outputChannel.appendLine(`内置 JAR 目录不存在: ${jarDir}`);
        return undefined;
    }

    const files = fs.readdirSync(jarDir);
    const jarFile = files.find(f => f.startsWith('iAppLSP') && f.endsWith('.jar'));
    
    if (jarFile) {
        return path.join(jarDir, jarFile);
    }

    outputChannel.appendLine(`内置 JAR 目录中未找到 JAR 文件: ${jarDir}`);
    return undefined;
}

export function deactivate(): Thenable<void> | undefined {
    if (!client) {
        return undefined;
    }
    
    statusBarItem.dispose();
    runStatusBarItem.dispose();
    outputChannel.dispose();
    
    return client.stop();
}
