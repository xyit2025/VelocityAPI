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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public static Javalin app;

    public static @Nullable QueryReturn query(int page){
        if (page < 0){
            return null;
        }
        int playerNumber = Config.server.getPlayerCount();
        List<String> players = new ArrayList<>();
        Collection<Player> rawPlayers = Config.server.getAllPlayers();
        for (Player player : rawPlayers){
            players.add(player.getUsername());
        }
        List<List<String>> l = partition(players, 10);
        if (page >= l.size()){
            QueryReturn q = new QueryReturn();
            q.setMessage(String.valueOf(l.size()));
            return q;
        }
        QueryReturn q = new QueryReturn();
        q.setPlayers(l.get(page));
        q.setPlayer_number(String.valueOf(playerNumber));
        return q;
    }

    public static <T> List<List<T>> partition(@NotNull List<T> list, int partitionCount) {
    int size = list.size();
    int chunkSize = (int) Math.ceil((double) size / partitionCount);
    return IntStream.range(0, partitionCount)
            .mapToObj(i -> list.subList(
                    i * chunkSize,
                    Math.min((i + 1) * chunkSize, size)))
            .collect(Collectors.toList());
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
