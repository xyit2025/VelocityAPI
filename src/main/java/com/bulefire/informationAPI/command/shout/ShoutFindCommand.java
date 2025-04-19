package com.bulefire.informationAPI.command.shout;

import com.bulefire.informationAPI.datdabase.PlayerDAO;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ShoutFindCommand implements SimpleCommand {
    @Override
    public void execute(@NotNull Invocation invocation) {
        if (invocation.source() instanceof Player player){
            int shout = PlayerDAO.getShoutByName(player.getUsername());
            if (shout == -2){
                player.sendMessage(Component.text("您还没有绑定过 QQ"));
                return;
            }
            invocation.source().sendMessage(Component.text("Your shout is " + shout));
            invocation.source().sendMessage(Component.translatable("informationapi.bind").arguments(Component.text(shout)));
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
