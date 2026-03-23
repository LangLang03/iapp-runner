import * as path from 'path';
import { Runner, BaseRunnerOptions } from './Runner';
import { IAppConfig } from '../config/IAppConfig';

export interface IAppRunnerOptions extends BaseRunnerOptions {
    scriptFile: string;
    config: IAppConfig;
}

export class IAppRunner extends Runner {
    private options!: IAppRunnerOptions;

    protected buildCommand(options: BaseRunnerOptions): { command: string; args: string[] } {
        const opts = options as IAppRunnerOptions;
        this.options = opts;

        const args: string[] = ['-cp', opts.classpath];
        
        if (opts.config.jvmArgs && opts.config.jvmArgs.length > 0) {
            args.push(...opts.config.jvmArgs);
        }
        
        args.push('cn.langlang.iapp.Main', opts.scriptFile);
        
        if (opts.config.javaArgs) {
            args.push(...opts.config.javaArgs);
        }

        return { command: opts.javaPath, args };
    }

    async start(options: IAppRunnerOptions): Promise<void> {
        this._onOutput.fire({ 
            type: 'stdout', 
            data: `运行 iApp 脚本: ${options.scriptFile}\n` 
        });
        
        return super.start(options);
    }
}
