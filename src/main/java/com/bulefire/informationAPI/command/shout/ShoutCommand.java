package com.bulefire.informationAPI.command.shout;

import com.bulefire.informationAPI.config.Config;
import com.bulefire.informationAPI.util.Suggests;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ShoutCommand implements SimpleCommand {
    private static final List<String> cmm = List.of("add","set","find");

    @Override
    public void execute(@NotNull Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 0){
            invocation.source().sendMessage(Component.translatable("shout.help.message"));
            return;
        }
        switch (args[0]){
            case "add" -> new ShoutAddCommand(Config.server).execute(invocation);
            case "set" -> new ShoutSetCommand(Config.server).execute(invocation);
            case "find" -> new ShoutFindCommand().execute(invocation);
            default -> invocation.source().sendMessage(Component.translatable("shout.help.message"));
        }
    }

    @Override
    public List<String> suggest(@NotNull Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 0){
            return List.of("add","set","find");
        }
        if (args.length == 1){
            String input = args[0].toLowerCase();
            return cmm.stream()
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return switch (args[0]) {
            case "add", "set", "find" -> Suggests.getPlayerNameSuggest(invocation, Config.server);
            default -> List.of();
        };
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.completedFuture(suggest(invocation));
    }

    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        return true;
        //return invocation.source().hasPermission("informationapi.shout.all");
    }
}
