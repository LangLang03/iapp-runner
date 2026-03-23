export interface YuWebConfig {
    port: number;
    debugMode: boolean;
    safeMode: boolean;
    preloadScripts: boolean;
    serveStaticFiles: boolean;
}

export interface IAppConfig {
    version: string;
    runDirectory: string;
    enableYuWeb: boolean;
    yuweb?: YuWebConfig;
    javaArgs?: string[];
    jvmArgs?: string[];
}

export const DEFAULT_YUWEB_CONFIG: YuWebConfig = {
    port: 8080,
    debugMode: false,
    safeMode: false,
    preloadScripts: false,
    serveStaticFiles: true
};

export const DEFAULT_IAPP_CONFIG: IAppConfig = {
    version: "1.0",
    runDirectory: ".",
    enableYuWeb: false,
    yuweb: DEFAULT_YUWEB_CONFIG,
    javaArgs: [],
    jvmArgs: []
};

export const CONFIG_FILE_NAME = ".iapprc.json";
