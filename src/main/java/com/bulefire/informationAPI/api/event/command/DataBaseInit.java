package com.bulefire.informationAPI.api.event.command;

import com.bulefire.informationAPI.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DataBaseInit {
    private final static Logger logger = LoggerFactory.getLogger(DataBaseInit.class);

    public static void init() throws SQLException {
        logger.info("init database tables");
        try {
            logger.info("loading mysql driver");
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("mysql driver not found");
            return;
        }
        logger.info("try to init tables");
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            DatabaseMetaData metaData = conn.getMetaData();

            String tableName = "playerqu";
            ResultSet tables = metaData.getTables(
                    null,
                    null,
                    tableName,
                    new String[]{"TABLE"}
            );

            if (!tables.next()) {
                logger.info("表不存在，正在创建表...");
                // 执行建表SQL（示例表结构，需按实际需求修改）
                String createTableSQL = "CREATE TABLE playerqu (" +
                        "qid VARCHAR(255) NOT NULL PRIMARY KEY," +
                        "uuid VARCHAR(36) NOT NULL, " +
                        "username VARCHAR(255) NOT NULL"+
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

                stmt.executeUpdate(createTableSQL);
                // 在executeUpdate后添加DDL验证
                // 验证表结构（可选）
                try (ResultSet afterCreation = metaData.getColumns(null, null, tableName, null)) {
                    if (!afterCreation.next()) {
                        throw new SQLException("表结构创建异常");
                    }
                }
            }
        } catch (SQLException e) {
            DatabaseUtil.handleDatabaseError(e, DataBaseInit.class);
        }
    }
}
