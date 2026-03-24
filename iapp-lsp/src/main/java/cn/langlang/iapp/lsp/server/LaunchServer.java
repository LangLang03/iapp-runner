package cn.langlang.iapp.lsp.server;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.InputStream;
import java.io.OutputStream;

public class LaunchServer {
    public static void main(String[] args) {
        boolean loadYuWeb = false;
        
        for (String arg : args) {
            if ("--yuweb".equals(arg) || "-y".equals(arg)) {
                loadYuWeb = true;
                break;
            }
        }
        
        try {
            IAppLanguageServer server = new IAppLanguageServer(loadYuWeb);
            
            InputStream in = System.in;
            OutputStream out = System.out;
            
            Launcher<LanguageClient> launcher = Launcher.createLauncher(
                server,
                LanguageClient.class,
                in,
                out
            );
            
            server.connect(launcher.getRemoteProxy());
            
            launcher.startListening().get();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
