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

public class ShoutSetCommand implements SimpleCommand {
    private final ProxyServer proxyServer;

    public ShoutSetCommand(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void execute(@NotNull Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length != 3) {
            invocation.source().sendMessage(Component.translatable("shout.set.help.message"));
        }
        String playerName = args[1];
        int shout = Integer.parseInt(args[2]);
        if (shout < 0){
            invocation.source().sendMessage(Component.translatable("shout.set.error.negative"));
            return;
        }
        PlayerDAO.updateShoutByName(playerName, shout);
        invocation.source().sendMessage(Component.translatable("shout.set.successful").arguments(Component.text(playerName), Component.text(shout)));
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
        //return true;
        return invocation.source().hasPermission("informationapi.shout.set");
    }
}
