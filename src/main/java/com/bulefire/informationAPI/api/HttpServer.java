package com.bulefire.informationAPI.api;

import com.bulefire.informationAPI.api.body.QueryReturn;
import com.bulefire.informationAPI.api.event.command.BlindCommand;
import com.bulefire.informationAPI.api.event.command.PlayerDAO;
import com.bulefire.informationAPI.api.event.command.SQLNoFoundException;
import com.bulefire.informationAPI.api.result.FindPlayerResult;
import com.bulefire.informationAPI.config.Config;
import com.velocitypowered.api.proxy.Player;
import io.javalin.Javalin;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public static Javalin app;

    public static @Nullable QueryReturn query(@NotNull String slice){
        int playerNumber = Config.server.getPlayerCount();
        List<String> players = new ArrayList<>();
        Collection<Player> rawPlayers = Config.server.getAllPlayers();
        for (Player player : rawPlayers){
            players.add(player.getUsername());
        }

        int[] index = getIndex(slice);
        if (index == null){
            return null;
        }

        players.subList(index[0],index[1]);

        QueryReturn q = new QueryReturn();
        q.setPlayers(players);
        q.setPlayer_number(String.valueOf(playerNumber));
        return q;
    }

    private static int @Nullable [] getIndex(@NotNull String slice){
        // 格式要求：[正整数:正整数] 且 start <= end
        Pattern pattern =  Pattern.compile( "^\\[(?<start>(?!0+\\d)[1-9]\\d*):(?<end>(?!0+\\d)[1-9]\\d*)]$");
        Matcher matcher = pattern.matcher(slice);
        if (!matcher.matches()){
            return null;
        }

        int[] indexes = new int[2];

        try {
            int start = Integer.parseInt(matcher.group("start"));
            int end = Integer.parseInt(matcher.group("end"));
            if (start >= 0 && end > start && end <= Config.server.getAllPlayers().size()){
                indexes[0] = start;
                indexes[1] = end;
                return indexes;
            }else {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static @NotNull FindPlayerResult find_player(String name){
        FindPlayerResult result = new FindPlayerResult();

        // 使用Velocity的API查询玩家
        Optional<Player> playerOptional = Config.server.getPlayer(name);

        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            result.setOnline(true);

            // 获取玩家所在服务器
            player.getCurrentServer().ifPresent(serverConnection -> result.setServer(serverConnection.getServerInfo().getName()));
        } else {
            result.setOnline(false);
            result.setServer("offline"); // 或返回"offline"
        }

        return result;
    }

    public static @NotNull String hh(@NotNull String qID, @NotNull String message) {
        String name;
        String text;
        try {
            name = PlayerDAO.getUserName(qID);
        }catch (SQLNoFoundException e){
            logger.info("未找到与 QID {} 相关的用户", qID);
            text = Config.getConfigJson().getFormat().replace("%username%","未知用户").replace("%message%",message);
            Config.server.sendMessage(Component.text(text));
            return "200";
        }catch (SQLException e){
            return "500";
        }
        if (name == null){
            return "500";
        }
        text = Config.getConfigJson().getFormat().replace("%username%",name).replace("%message%",message);
        Config.server.sendMessage(Component.text(text));
        return "200";
    }

    public static boolean blind(@NotNull String qID, @NotNull String code){
        logger.info("qID: {} code: {}", qID, code);
        BlindCommand.Result result = BlindCommand.blind(code);
        if (!result.valid){
            logger.info("验证码错误");
            return false;
        }
        UUID uuid = result.uuid;
        saveToDataBase(qID,uuid,result.username);
        return true;
    }

    private static void saveToDataBase(@NotNull String qID, @NotNull UUID uuid, @NotNull String username){
        logger.info("try to save {},{} to data base", qID, uuid);
        try {
            PlayerDAO.saveVerification(qID, uuid, username);
        } catch (SQLException e) {
            logger.error("save verification error", e);
        }
    }
}
