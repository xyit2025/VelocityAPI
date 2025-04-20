package com.bulefire.informationAPI.api;

import com.bulefire.informationAPI.api.body.QueryReturn;
import com.bulefire.informationAPI.command.bind.BindCommand;
import com.bulefire.informationAPI.datdabase.PlayerDAO;
import com.bulefire.informationAPI.datdabase.SQLNoFoundException;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public static Javalin app;

    private static final Map<String, Long> cooldownMap = new ConcurrentHashMap<>();
    private static final long COOLDOWN_TIME = Config.getConfigJson().getCooldownTime(); // 5s间隔

    public static @Nullable QueryReturn query(@NotNull String server, int page){
        if (page < 0){
            return null;
        }

        AtomicInteger playerNumber = new AtomicInteger();
        AtomicReference<List<List<String>>> l = new AtomicReference<>();
        QueryReturn q = new QueryReturn();

        if (server.equals("all")){
            playerNumber.set(Config.server.getPlayerCount());
            List<String> players = new ArrayList<>();
            Collection<Player> rawPlayers = Config.server.getAllPlayers();
            for (Player player : rawPlayers){
                players.add(player.getUsername());
            }
            l.set(partition(players, 10));
            q.setTotalPage(l.get().size());
        }else {
            Config.server.getAllServers().forEach(serverInfo -> {
                if (serverInfo.getServerInfo().getName().equals(server)){
                    playerNumber.set(serverInfo.getPlayersConnected().size());
                    List<String> players = new ArrayList<>();
                    serverInfo.getPlayersConnected().forEach(player -> players.add(player.getUsername()));
                    l.set(partition(players, 10));
                    q.setTotalPage(l.get().size());
                }
            });
        }
        if (!(l.get() == null)) {
            if (l.get().isEmpty()){
                q.setTotalPage(0);
                q.setPlayers(null);
            }else if (!(page > (l.get().size()-1))) {
                q.setPlayers(l.get().get(page));
            } else {
                q.setPlayers(null);
            }
        }else {
            q.setPlayers(null);
            q.setTotalPage(-1);
            q.setPlayer_number(-1);
        }
        q.setPlayer_number(playerNumber.get());
        return q;
    }

    public static <T> List<List<T>> partition(@NotNull List<T> list, int chunkSize) {
        int size = list.size();
        // 计算实际需要的分区数量（向上取整）
        int partitionCount = (int) Math.ceil((double) size / chunkSize);

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
        Long lastCall = cooldownMap.get(qID);
        if (lastCall != null && System.currentTimeMillis() - lastCall < COOLDOWN_TIME) {
            return "429 | " + (COOLDOWN_TIME-(System.currentTimeMillis() - lastCall));
        }
        String name;
        int shout;
        String text;
        try {
            name = PlayerDAO.getUserNameByQID(qID);
            shout = PlayerDAO.getShoutByQID(qID);
            if (shout <= 0){
                return "409";
            }
            Config.server.sendMessage(Component.translatable("hh.server_show.format").arguments(Component.text(name), Component.text(message)));
            PlayerDAO.updateShoutByQID(qID,shout-1);
        }catch (SQLNoFoundException e){
            logger.info("未找到与 QID {} 相关的用户", qID);
            return "403";
        }catch (SQLException e){
            logger.error("数据库错误", e);
            return "500";
        }
        cooldownMap.put(qID, System.currentTimeMillis());
        return "200 | " + shout;
    }

    public static boolean blind(@NotNull String qID, @NotNull String code){
        logger.info("qID: {} code: {}", qID, code);
        BindCommand.Result result = BindCommand.blind(code);
        if (!result.valid){
            logger.info("验证码错误");
            return false;
        }
        UUID uuid = result.uuid;
        Optional<Player> player = Config.server.getPlayer(uuid);
        player.ifPresent(value -> value.sendMessage(Component.translatable("验证成功")));
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
