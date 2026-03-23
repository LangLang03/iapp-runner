import * as path from 'path';
import * as fs from 'fs';
import { IAppConfig, YuWebConfig, DEFAULT_IAPP_CONFIG, DEFAULT_YUWEB_CONFIG } from './IAppConfig';

export interface ValidationResult {
    valid: boolean;
    errors: string[];
    warnings: string[];
}

export class ConfigValidator {
    static validate(config: unknown, workspacePath: string): ValidationResult {
        const errors: string[] = [];
        const warnings: string[] = [];

        if (!config || typeof config !== 'object') {
            return { valid: false, errors: ['配置文件格式无效'], warnings: [] };
        }

        const cfg = config as Record<string, unknown>;

        if (typeof cfg.version !== 'string') {
            errors.push('缺少 version 字段或类型错误');
        }

        if (typeof cfg.runDirectory !== 'string') {
            errors.push('缺少 runDirectory 字段或类型错误');
        } else {
            const runDir = path.resolve(workspacePath, cfg.runDirectory);
            if (!fs.existsSync(runDir)) {
                errors.push(`运行目录不存在: ${cfg.runDirectory}`);
            }
        }

        if (typeof cfg.enableYuWeb !== 'boolean') {
            errors.push('缺少 enableYuWeb 字段或类型错误');
        }

        if (cfg.yuweb !== undefined) {
            const yuwebResult = this.validateYuWebConfig(cfg.yuweb as Record<string, unknown>);
            errors.push(...yuwebResult.errors);
            warnings.push(...yuwebResult.warnings);
        }

        if (cfg.javaArgs !== undefined && !Array.isArray(cfg.javaArgs)) {
            errors.push('javaArgs 必须是数组');
        }

        if (cfg.jvmArgs !== undefined && !Array.isArray(cfg.jvmArgs)) {
            errors.push('jvmArgs 必须是数组');
        }

        return {
            valid: errors.length === 0,
            errors,
            warnings
        };
    }

    private static validateYuWebConfig(yuweb: Record<string, unknown>): ValidationResult {
        const errors: string[] = [];
        const warnings: string[] = [];

        if (yuweb.port !== undefined) {
            if (typeof yuweb.port !== 'number' || yuweb.port < 1 || yuweb.port > 65535) {
                errors.push(`端口号无效: ${yuweb.port} (应为 1-65535)`);
            } else if (yuweb.port < 1024) {
                warnings.push(`端口号 ${yuweb.port} 需要管理员权限`);
            }
        }

        if (yuweb.debugMode !== undefined && typeof yuweb.debugMode !== 'boolean') {
            errors.push('yuweb.debugMode 必须是布尔值');
        }

        if (yuweb.safeMode !== undefined && typeof yuweb.safeMode !== 'boolean') {
            errors.push('yuweb.safeMode 必须是布尔值');
        }

        if (yuweb.preloadScripts !== undefined && typeof yuweb.preloadScripts !== 'boolean') {
            errors.push('yuweb.preloadScripts 必须是布尔值');
        }

        if (yuweb.serveStaticFiles !== undefined && typeof yuweb.serveStaticFiles !== 'boolean') {
            errors.push('yuweb.serveStaticFiles 必须是布尔值');
        }

        return { valid: errors.length === 0, errors, warnings };
    }

    static mergeWithDefaults(config: Partial<IAppConfig>): IAppConfig {
        return {
            version: config.version || DEFAULT_IAPP_CONFIG.version,
            runDirectory: config.runDirectory || DEFAULT_IAPP_CONFIG.runDirectory,
            enableYuWeb: config.enableYuWeb ?? DEFAULT_IAPP_CONFIG.enableYuWeb,
            yuweb: {
                ...DEFAULT_YUWEB_CONFIG,
                ...(config.yuweb || {})
            },
            javaArgs: config.javaArgs || DEFAULT_IAPP_CONFIG.javaArgs,
            jvmArgs: config.jvmArgs || DEFAULT_IAPP_CONFIG.jvmArgs
        };
    }

    static hasYuWebChanged(oldConfig: IAppConfig | null, newConfig: IAppConfig): boolean {
        if (!oldConfig) return true;
        return oldConfig.enableYuWeb !== newConfig.enableYuWeb;
    }
}
