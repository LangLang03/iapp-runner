import * as path from 'path';
import * as fs from 'fs';
import * as cp from 'child_process';
import { workspace, ExtensionContext, window, OutputChannel, commands, StatusBarAlignment, StatusBarItem, ConfigurationChangeEvent } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions, Executable, ExecutableOptions } from 'vscode-languageclient/node';

let client: LanguageClient | undefined;
let outputChannel: OutputChannel;
let statusBarItem: StatusBarItem;

const JAVA_MIN_VERSION = 17;
const JAVA_DOWNLOAD_URL = 'https://www.oracle.com/java/technologies/downloads/#java17';

export function activate(context: ExtensionContext) {
    outputChannel = window.createOutputChannel('iApp 语言服务器');
    outputChannel.appendLine('iApp 扩展正在激活...');
    
    statusBarItem = window.createStatusBarItem(StatusBarAlignment.Right, 100);
    statusBarItem.text = '$(sync~spin) iApp LSP';
    statusBarItem.tooltip = 'iApp 语言服务器启动中...';
    statusBarItem.show();
    context.subscriptions.push(statusBarItem);

    const restartCommand = commands.registerCommand('iapp.restartServer', async () => {
        outputChannel.appendLine('收到重启命令');
        await restartLanguageServer(context);
    });

    const showOutputCommand = commands.registerCommand('iapp.showOutput', () => {
        outputChannel.show();
    });

    context.subscriptions.push(restartCommand, showOutputCommand);
    outputChannel.appendLine('命令已注册');

    context.subscriptions.push(
        workspace.onDidChangeConfiguration((e: ConfigurationChangeEvent) => {
            if (e.affectsConfiguration('iapp.lsp.enableYuWeb')) {
                outputChannel.appendLine('YuWeb 配置已更改，正在重启语言服务器...');
                restartLanguageServer(context);
            }
        })
    );

    startLanguageServer(context);
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

        const serverOptions = createServerOptions(context);
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
                enableYuWeb: workspace.getConfiguration('iapp').get('lsp.enableYuWeb', false)
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
            const uri = require('vscode').Uri.parse(JAVA_DOWNLOAD_URL);
            require('vscode').env.openExternal(uri);
        }
    });
}

function createServerOptions(context: ExtensionContext): ServerOptions {
    const config = workspace.getConfiguration('iapp');
    const javaPath = config.get<string>('java.path', 'java');
    const jarPath = config.get<string>('lsp.jarPath', '');
    const enableYuWeb = config.get<boolean>('lsp.enableYuWeb', false);
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
        throw new Error('未找到 iAppLSP JAR 文件。请在设置中配置 iapp.lsp.jarPath 或确保扩展包含内置 JAR。');
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
    outputChannel.dispose();
    
    return client.stop();
}
