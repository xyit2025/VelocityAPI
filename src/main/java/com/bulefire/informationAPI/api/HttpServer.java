package com.bulefire.informationAPI.api;

import com.bulefire.informationAPI.api.result.FindPlayerResult;
import com.bulefire.informationAPI.config.Config;
import com.velocitypowered.api.proxy.Player;
import io.javalin.Javalin;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HttpServer {
    public static Javalin app;

    public static @NotNull String query(){
        int playerNumber = Config.server.getPlayerCount();
        return "{\"player_number\":" +
                playerNumber +
                "}";
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

    public static void hh(String message){
        Config.server.sendMessage(Component.text("<全服喊话>"+message));
    }
}
