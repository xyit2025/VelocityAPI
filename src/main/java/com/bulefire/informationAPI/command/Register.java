package com.bulefire.informationAPI.command;

import com.bulefire.informationAPI.command.bind.BindCommand;
import com.bulefire.informationAPI.command.shout.ShoutAddCommand;
import com.bulefire.informationAPI.command.shout.ShoutFindCommand;
import com.bulefire.informationAPI.command.shout.ShoutSetCommand;
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
        CommandMeta setMeta = cm.metaBuilder("set")
                .aliases("s")
                .build();
        cm.register(setMeta, new ShoutSetCommand(server));
        //add
        CommandMeta addMeta = cm.metaBuilder("add")
                .aliases("a")
                .build();
        cm.register(addMeta, new ShoutAddCommand(server));
        //find
        CommandMeta findMeta = cm.metaBuilder("find")
                .aliases("f")
                .build();
        cm.register(findMeta, new ShoutFindCommand());
    }
}
