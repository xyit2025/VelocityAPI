package com.bulefire.informationAPI.util;

import com.bulefire.informationAPI.config.DataBaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataBaseConfig.getDatabaseUrl());
        config.setUsername(DataBaseConfig.getDatabaseUsername());
        config.setPassword(DataBaseConfig.getDatabasePassword());
        config.setMaximumPoolSize(DataBaseConfig.getDatabasePoolSize());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void handleDatabaseError(@NotNull SQLException e, @NotNull Class<?> klass) {
        Logger logger = LoggerFactory.getLogger(klass);
        // 通过错误码精准识别（MySQL访问拒绝错误码为1045）
        if (e.getErrorCode() == 1045) {
            logger.error("认证失败: 用户名或密码错误");
            logger.error("错误详情: {}", e.getMessage());
            // 可追加密码重置引导或配置检查提示
        } else {
            logger.error("数据库异常: {}", e.getMessage());
        }
    }
}
