package com.bulefire.informationAPI.config;

public class DataBaseConfig {
    // 新增数据库配置获取方法
    public static String getDatabaseUrl() {
        return Config.getConfigJson().getDatabase().getUrl();
    }

    public static String getDatabaseUsername() {
        return Config.getConfigJson().getDatabase().getUsername();
    }

    public static String getDatabasePassword() {
        return Config.getConfigJson().getDatabase().getPassword();
    }

    public static int getDatabasePoolSize() {
        return Config.getConfigJson().getDatabase().getPoolSize();
    }
}
