import { Runner, BaseRunnerOptions } from './Runner';
import { IAppConfig, YuWebConfig } from '../config/IAppConfig';

export interface YuWebRunnerOptions extends BaseRunnerOptions {
    config: IAppConfig;
}

export class YuWebRunner extends Runner {
    private options!: YuWebRunnerOptions;

    protected buildCommand(options: BaseRunnerOptions): { command: string; args: string[] } {
        const opts = options as YuWebRunnerOptions;
        this.options = opts;

        const yuwebConfig: YuWebConfig = opts.config.yuweb || {
            port: 8080,
            debugMode: false,
            safeMode: false,
            preloadScripts: false,
            serveStaticFiles: true
        };

        const args: string[] = ['-cp', opts.classpath];
        
        if (opts.config.jvmArgs && opts.config.jvmArgs.length > 0) {
            args.push(...opts.config.jvmArgs);
        }
        
        args.push('cn.langlang.yuweb.server.Main');

        args.push('--port', String(yuwebConfig.port));

        if (yuwebConfig.debugMode) {
            args.push('--debug');
        }

        if (yuwebConfig.safeMode) {
            args.push('--safe');
        }

        if (yuwebConfig.preloadScripts) {
            args.push('--preload');
        }

        if (!yuwebConfig.serveStaticFiles) {
            args.push('--no-static');
        }

        args.push(opts.config.runDirectory);

        if (opts.config.javaArgs) {
            args.push(...opts.config.javaArgs);
        }

        return { command: opts.javaPath, args };
    }

    async start(options: YuWebRunnerOptions): Promise<void> {
        const yuwebConfig = options.config.yuweb || {
            port: 8080,
            debugMode: false,
            safeMode: false,
            preloadScripts: false,
            serveStaticFiles: true
        };

        this._onOutput.fire({ 
            type: 'stdout', 
            data: `启动 YuWeb 服务器 (端口: ${yuwebConfig.port})\n` 
        });

        return super.start(options);
    }
}
