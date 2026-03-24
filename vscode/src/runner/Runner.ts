import * as cp from 'child_process';
import { Event, EventEmitter, window, Terminal, TerminalOptions, Disposable } from 'vscode';

export interface BaseRunnerOptions {
    javaPath: string;
    classpath: string;
    cwd: string;
    env?: NodeJS.ProcessEnv;
    useNewTerminal?: boolean;
}

export interface OutputEvent {
    type: 'stdout' | 'stderr';
    data: string;
}

export abstract class Runner implements Disposable {
    protected process: cp.ChildProcess | null = null;
    protected terminal: Terminal | null = null;
    protected _isRunning: boolean = false;
    protected readonly _onOutput = new EventEmitter<OutputEvent>();
    protected readonly _onExit = new EventEmitter<number | null>();
    
    readonly onOutput: Event<OutputEvent> = this._onOutput.event;
    readonly onExit: Event<number | null> = this._onExit.event;

    get isRunning(): boolean {
        return this._isRunning;
    }

    protected buildCommand(options: BaseRunnerOptions): { command: string; args: string[] } {
        throw new Error('buildCommand must be implemented by subclass');
    }

    async start(options: BaseRunnerOptions): Promise<void> {
        if (this._isRunning && this.process) {
            throw new Error('Runner is already running');
        }

        const { command, args } = this.buildCommand(options);
        const fullCommand = `${command} ${args.map(a => a.includes(' ') ? `"${a}"` : a).join(' ')}`;

        this._onOutput.fire({ type: 'stdout', data: `启动命令: ${fullCommand}\n` });

        if (options.useNewTerminal !== false) {
            return this.startInTerminal(options, fullCommand);
        } else {
            return this.startInProcess(options, command, args);
        }
    }

    private async startInTerminal(options: BaseRunnerOptions, fullCommand: string): Promise<void> {
        const terminalOptions: TerminalOptions = {
            name: 'iApp 运行',
            cwd: options.cwd,
            env: options.env,
            shellArgs: [],
            hideFromUser: false
        };

        this.terminal = window.createTerminal(terminalOptions);
        this.terminal.show(true);
        this.terminal.sendText(fullCommand);
        this._isRunning = false;

        window.onDidCloseTerminal((closedTerminal) => {
            if (closedTerminal === this.terminal) {
                this.terminal = null;
            }
        });
    }

    private async startInProcess(options: BaseRunnerOptions, command: string, args: string[]): Promise<void> {
        return new Promise((resolve, reject) => {
            try {
                this.process = cp.spawn(command, args, {
                    cwd: options.cwd,
                    env: { ...process.env, ...options.env },
                    shell: true
                });

                this._isRunning = true;

                this.process.stdout?.on('data', (data: Buffer) => {
                    this._onOutput.fire({ type: 'stdout', data: data.toString() });
                });

                this.process.stderr?.on('data', (data: Buffer) => {
                    this._onOutput.fire({ type: 'stderr', data: data.toString() });
                });

                this.process.on('error', (err: Error) => {
                    this._onOutput.fire({ type: 'stderr', data: `进程错误: ${err.message}\n` });
                    this._isRunning = false;
                    reject(err);
                });

                this.process.on('exit', (code: number | null) => {
                    this._isRunning = false;
                    this._onExit.fire(code);
                    if (code !== 0 && code !== null) {
                        this._onOutput.fire({ type: 'stderr', data: `进程退出，代码: ${code}\n` });
                    }
                });

                resolve();
            } catch (e) {
                this._isRunning = false;
                reject(e);
            }
        });
    }

    stop(): Promise<void> {
        return new Promise((resolve) => {
            if (this.terminal) {
                this.terminal.dispose();
                this.terminal = null;
                resolve();
                return;
            }

            if (!this.process || !this._isRunning) {
                resolve();
                return;
            }

            this._onOutput.fire({ type: 'stdout', data: '正在停止进程...\n' });

            this.process.on('exit', () => {
                this._isRunning = false;
                resolve();
            });

            try {
                if (process.platform === 'win32') {
                    cp.execSync(`taskkill /pid ${this.process.pid} /T /F`);
                } else {
                    this.process.kill('SIGTERM');
                }
            } catch {
                this._isRunning = false;
                resolve();
            }
        });
    }

    dispose(): void {
        if (this._isRunning) {
            this.stop();
        }
        this._onOutput.dispose();
        this._onExit.dispose();
    }
}
