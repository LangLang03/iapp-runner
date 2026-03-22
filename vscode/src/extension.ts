import * as path from 'path';
import * as fs from 'fs';
import { workspace, ExtensionContext, window, OutputChannel, commands, StatusBarAlignment, StatusBarItem } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions, TransportKind, Executable, ExecutableOptions } from 'vscode-languageclient/node';

let client: LanguageClient | undefined;
let outputChannel: OutputChannel;
let statusBarItem: StatusBarItem;

export function activate(context: ExtensionContext) {
    outputChannel = window.createOutputChannel('iApp Language Server');
    
    statusBarItem = window.createStatusBarItem(StatusBarAlignment.Right, 100);
    statusBarItem.text = '$(sync~spin) iApp LSP';
    statusBarItem.tooltip = 'iApp Language Server is starting...';
    statusBarItem.show();
    context.subscriptions.push(statusBarItem);

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
        'iApp Language Server',
        serverOptions,
        clientOptions
    );

    client.start().then(() => {
        statusBarItem.text = '$(check) iApp LSP';
        statusBarItem.tooltip = 'iApp Language Server is running';
        outputChannel.appendLine('iApp Language Server started successfully');
    }).catch((error) => {
        statusBarItem.text = '$(error) iApp LSP';
        statusBarItem.tooltip = 'iApp Language Server failed to start';
        outputChannel.appendLine(`Failed to start iApp Language Server: ${error}`);
        window.showErrorMessage(`Failed to start iApp Language Server: ${error}`);
    });

    const restartCommand = commands.registerCommand('iapp.restartServer', async () => {
        if (client) {
            statusBarItem.text = '$(sync~spin) iApp LSP';
            statusBarItem.tooltip = 'Restarting iApp Language Server...';
            
            await client.stop();
            outputChannel.appendLine('iApp Language Server stopped');
            
            const newServerOptions = createServerOptions(context);
            client = new LanguageClient(
                'iappLanguageServer',
                'iApp Language Server',
                newServerOptions,
                clientOptions
            );
            
            await client.start();
            statusBarItem.text = '$(check) iApp LSP';
            statusBarItem.tooltip = 'iApp Language Server is running';
            outputChannel.appendLine('iApp Language Server restarted successfully');
            window.showInformationMessage('iApp Language Server restarted');
        }
    });

    const showOutputCommand = commands.registerCommand('iapp.showOutput', () => {
        outputChannel.show();
    });

    context.subscriptions.push(restartCommand, showOutputCommand);
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
    } else {
        actualJarPath = findBundledJar(context);
    }

    if (!actualJarPath) {
        throw new Error('iAppLSP JAR file not found. Please set iapp.lsp.jarPath in settings or ensure the JAR is bundled with the extension.');
    }

    outputChannel.appendLine(`Using JAR: ${actualJarPath}`);

    const args: string[] = ['-jar', actualJarPath];
    if (enableYuWeb) {
        args.push('--yuweb');
    }
    if (debug) {
        args.push('--debug');
    }

    const executable: Executable = {
        command: javaPath,
        args: args,
        options: {
            cwd: workspace.rootPath || process.cwd()
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
        return undefined;
    }

    const files = fs.readdirSync(jarDir);
    const jarFile = files.find(f => f.startsWith('iAppLSP') && f.endsWith('.jar'));
    
    if (jarFile) {
        return path.join(jarDir, jarFile);
    }

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
