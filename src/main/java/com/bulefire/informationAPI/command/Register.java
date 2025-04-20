package com.bulefire.informationAPI.command;

import com.bulefire.informationAPI.command.bind.BindCommand;
import com.bulefire.informationAPI.command.shout.ShoutCommand;
import com.bulefire.informationAPI.command.shout.ShoutFindPlayerCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;

public class Register {
    public static void register(@NotNull ProxyServer server) {
        CommandManager cm = server.getCommandManager();
        // bind
        CommandMeta bdMeta = cm.metaBuilder("bind")
                .aliases("bd")
                .build();
        cm.register(bdMeta, new BindCommand());
        // shout
        //set
        CommandMeta shoutMeta = cm.metaBuilder("shout")
                .aliases("sh")
                .build();
        cm.register(shoutMeta, new ShoutCommand());

        CommandMeta shoutFindMeta = cm.metaBuilder("find")
                .build();
        cm.register(shoutFindMeta, new ShoutFindPlayerCommand());
    }
}
