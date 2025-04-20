package com.bulefire.informationAPI.command.shout;

import com.bulefire.informationAPI.datdabase.PlayerDAO;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ShoutFindPlayerCommand implements SimpleCommand {
    @Override
    public void execute(@NotNull Invocation invocation) {
        String[] args = invocation.arguments();
        if (invocation.source() instanceof Player player){
            int shout = PlayerDAO.getShoutByName(player.getUsername());
            if (shout == -2){
                player.sendMessage(Component.translatable("shout.find.bind.qq.not"));
                return;
            }
            invocation.source().sendMessage(Component.translatable("shout.find.message").arguments(Component.text(shout)));

        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return SimpleCommand.super.suggestAsync(invocation);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return SimpleCommand.super.hasPermission(invocation);
    }
}
