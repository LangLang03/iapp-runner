import * as path from 'path';
import { workspace, window, OutputChannel, WorkspaceFolder, Disposable } from 'vscode';
import { IAppConfig } from '../config/IAppConfig';
import { Runner, OutputEvent, BaseRunnerOptions } from './Runner';
import { IAppRunner, IAppRunnerOptions } from './IAppRunner';
import { YuWebRunner, YuWebRunnerOptions } from './YuWebRunner';

export interface RunnerManagerOptions {
    javaPath: string;
    classpath: string;
    outputChannel: OutputChannel;
}

export class RunnerManager implements Disposable {
    private runners: Map<string, Runner> = new Map();
    private options: RunnerManagerOptions;
    private outputChannel: OutputChannel;

    constructor(options: RunnerManagerOptions) {
        this.options = options;
        this.outputChannel = options.outputChannel;
    }

    async run(folder: WorkspaceFolder, config: IAppConfig, scriptFile?: string): Promise<void> {
        const folderPath = folder.uri.fsPath;
        
        if (this.runners.has(folderPath)) {
            const existing = this.runners.get(folderPath)!;
            if (existing.isRunning) {
                window.showWarningMessage('项目正在运行中，请先停止');
                return;
            }
        }

        let runner: Runner;
        
        if (config.enableYuWeb) {
            runner = new YuWebRunner();
        } else {
            if (!scriptFile) {
                window.showErrorMessage('请选择要运行的脚本文件');
                return;
            }
            runner = new IAppRunner();
        }

        runner.onOutput((event: OutputEvent) => {
            if (event.type === 'stdout') {
                this.outputChannel.append(event.data);
            } else {
                this.outputChannel.append(`[错误] ${event.data}`);
            }
        });

        runner.onExit((code) => {
            if (code === 0 || code === null) {
                this.outputChannel.appendLine('进程已退出');
            } else {
                this.outputChannel.appendLine(`进程异常退出，代码: ${code}`);
            }
            this.runners.delete(folderPath);
        });

        this.runners.set(folderPath, runner);

        try {
            const baseOptions: BaseRunnerOptions = {
                javaPath: this.options.javaPath,
                classpath: this.options.classpath,
                cwd: folderPath
            };

            if (config.enableYuWeb) {
                const yuwebOptions: YuWebRunnerOptions = {
                    ...baseOptions,
                    config: config
                };
                await (runner as YuWebRunner).start(yuwebOptions);
            } else {
                const iappOptions: IAppRunnerOptions = {
                    ...baseOptions,
                    scriptFile: scriptFile!,
                    config: config
                };
                await (runner as IAppRunner).start(iappOptions);
            }
        } catch (e) {
            this.runners.delete(folderPath);
            const msg = e instanceof Error ? e.message : String(e);
            window.showErrorMessage(`启动失败: ${msg}`);
            this.outputChannel.appendLine(`启动失败: ${msg}`);
        }
    }

    async stop(folder: WorkspaceFolder): Promise<void> {
        const folderPath = folder.uri.fsPath;
        const runner = this.runners.get(folderPath);
        
        if (runner && runner.isRunning) {
            await runner.stop();
            this.runners.delete(folderPath);
        }
    }

    isRunning(folder: WorkspaceFolder): boolean {
        const runner = this.runners.get(folder.uri.fsPath);
        return runner?.isRunning || false;
    }

    getRunner(folder: WorkspaceFolder): Runner | undefined {
        return this.runners.get(folder.uri.fsPath);
    }

    async stopAll(): Promise<void> {
        const stops: Promise<void>[] = [];
        for (const [_, runner] of this.runners) {
            if (runner.isRunning) {
                stops.push(runner.stop());
            }
        }
        await Promise.all(stops);
        this.runners.clear();
    }

    updateOptions(options: Partial<RunnerManagerOptions>): void {
        this.options = { ...this.options, ...options };
    }

    dispose(): void {
        this.stopAll();
    }
}
