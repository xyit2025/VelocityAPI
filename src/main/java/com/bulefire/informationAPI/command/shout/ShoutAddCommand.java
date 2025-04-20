package com.bulefire.informationAPI.command.shout;

import com.bulefire.informationAPI.datdabase.PlayerDAO;
import com.bulefire.informationAPI.util.Suggests;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ShoutAddCommand implements SimpleCommand {
    private final ProxyServer proxyServer;

    public ShoutAddCommand(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void execute(@NotNull Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length != 3){
            invocation.source().sendMessage(Component.translatable("shout.add.help.message"));
        }

        String playerName = args[1];
        int shout = Integer.parseInt(args[2]);
        PlayerDAO.updateShoutByName(playerName,PlayerDAO.getShoutByName(playerName) + shout);
        invocation.source().sendMessage(Component.translatable("shout.add.successful").arguments(Component.text(shout), Component.text(playerName)));
        invocation.source().sendMessage(Component.translatable("shout.add.successful.update").arguments(Component.text(PlayerDAO.getShoutByName(playerName))));
    }

    @Override
    public List<String> suggest(@NotNull Invocation invocation) {
        return Suggests.getPlayerNameSuggest(invocation, proxyServer);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.completedFuture(suggest(invocation));
    }

    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        return invocation.source().hasPermission("informationapi.shout.add");
    }
}
