package com.bulefire.informationAPI.datdabase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerDAO {
    private static final Logger logger = LoggerFactory.getLogger(PlayerDAO.class);

    public static void saveVerification(@NotNull String qid, @NotNull UUID uuid, @NotNull String username) throws SQLException {
        String sql = "INSERT INTO playerqu (qid, uuid, username) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE uuid = VALUES(uuid),username = VALUES(username)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            logger.info("正在保存验证信息到数据库...");
            logger.info("qID:{} uuid:{} username:{}",qid,uuid,username);
            pstmt.setString(1, qid);
            pstmt.setString(2, uuid.toString());
            pstmt.setString(3, username);
            pstmt.executeUpdate();
        }catch (SQLException e){
            logger.error(e.getMessage());
        }
        logger.info("验证信息保存成功！");
    }

    public static void updateShoutByName(@NotNull String name, int shout){
        String sql = "UPDATE player_db.playerqu SET shout = ? WHERE username = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            logger.info("正在更新 玩家: {} 的 shout 值为: {}", name, shout);
            pstmt.setInt(1, shout);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
            int affectedRows = pstmt.executeUpdate();
            logger.debug("name -> shout update 受影响行数: {}", affectedRows);
        }catch (SQLException e){
            logger.error(e.getMessage());
        }
        logger.info("name -> shout 信息保存成功！");
    }

    public static void updateShoutByQID(@NotNull String qID, int shout) throws SQLException {
        String sql = "UPDATE player_db.playerqu SET shout = ? WHERE qid = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            logger.info("正在更新 QID: {} 的 shout 值为: {}", qID, shout);
            pstmt.setInt(1, shout);
            pstmt.setString(2, qID);
            pstmt.executeUpdate();
            int affectedRows = pstmt.executeUpdate();
            logger.debug("qID -> shout update 受影响行数: {}", affectedRows);
        }catch (SQLException e){
            logger.error(e.getMessage());
        }
        logger.info("qID -> shout 信息保存成功！");
    }

    public static int getShoutByName(@NotNull String name){
        String sql = "SELECT shout FROM playerqu WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("shout");
                }
            } catch (SQLException e){
                DatabaseUtil.handleDatabaseError(e, PlayerDAO.class);
                return -100;
            }
        } catch (SQLException e){
            DatabaseUtil.handleDatabaseError(e, PlayerDAO.class);
            return -100;
        }
        return -2;
    }

    public static @NotNull UUID getUserUUIDByQID(@NotNull String qID) throws SQLException {
        String sql = "SELECT uuid FROM playerqu WHERE qid = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, qID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("uuid"));
                }
            }
        } catch (SQLException e) {
            DatabaseUtil.handleDatabaseError(e, PlayerDAO.class);
            throw e; // 向上抛出异常供调用方处理
        }
        throw new SQLNoFoundException("未找到与 QID " + qID + " 相关的用户");
    }

    public static @NotNull String getUserNameByQID(@NotNull String qID) throws SQLException {
        String sql = "SELECT username FROM playerqu WHERE qid = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, qID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("查询到用户名：{}",rs.getString("username"));
                    return rs.getString("username");
                }
            }
        }
        throw new SQLNoFoundException("未找到与 QID " + qID + " 相关的用户");
    }

    public static @Nullable String getQIDByUUID(@NotNull UUID uuid) throws SQLException {
        String sql = "SELECT qid FROM playerqu WHERE uuid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("qid");
                }
            }
        }
        return null;
    }

    public static void deleteByUUID(@NotNull UUID uuid) throws SQLException {
        String sql = "DELETE FROM playerqu WHERE uuid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.executeUpdate();
        }
    }

    public static @Nullable String getUserNameByUUID(@NotNull UUID uuid) throws SQLException {
        String sql = "SELECT username FROM playerqu WHERE uuid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        }
        return null;
    }

    public static int getShoutByQID(@NotNull String qID) throws SQLException{
        String sql = "SELECT shout FROM playerqu WHERE qid = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, qID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("查询到 {} 剩余 {} 话筒",qID,rs.getInt("shout"));
                    return rs.getInt("shout");
                }
            }
        }
        return -1;
    }
}
