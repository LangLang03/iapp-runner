import * as path from 'path';
import * as fs from 'fs';
import { workspace, ExtensionContext, window, OutputChannel, commands, StatusBarAlignment, StatusBarItem } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions, Executable, ExecutableOptions } from 'vscode-languageclient/node';

let client: LanguageClient | undefined;
let outputChannel: OutputChannel;
let statusBarItem: StatusBarItem;

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
    });

    const showOutputCommand = commands.registerCommand('iapp.showOutput', () => {
        outputChannel.show();
    });

    context.subscriptions.push(restartCommand, showOutputCommand);
    outputChannel.appendLine('命令已注册');

    startLanguageServer(context);
}

function startLanguageServer(context: ExtensionContext) {
    try {
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
