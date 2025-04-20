package com.bulefire.informationAPI.util;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Suggests {
    @NotNull
    public static List<String> getPlayerNameSuggest(@NotNull SimpleCommand.Invocation invocation, ProxyServer proxyServer) {
        String[] args = invocation.arguments();

        if (args.length == 1) {
            return proxyServer.getAllPlayers().stream()
                    .map(Player::getUsername)
                    .collect(Collectors.toList());
        }else if (args.length == 2) {
            String input = args[1].toLowerCase();
            if (input.isEmpty()){
                return proxyServer.getAllPlayers().stream()
                        .map(Player::getUsername)
                        .collect(Collectors.toList());
            }
            return proxyServer.getAllPlayers().stream()
                    .map(Player::getUsername)
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
