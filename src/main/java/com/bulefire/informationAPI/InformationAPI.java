package com.bulefire.informationAPI;

import com.bulefire.informationAPI.api.Init;
import com.bulefire.informationAPI.api.event.command.BindCommand;
import com.bulefire.informationAPI.api.event.command.DataBaseInit;
import com.bulefire.informationAPI.config.Config;
import com.bulefire.informationAPI.util.DatabaseUtil;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

/***
 * ,--.         ,---.                                  ,--.  ,--.                 ,---.  ,------. ,--.
 * |  |,--,--, /  .-' ,---. ,--.--.,--,--,--. ,--,--.,-'  '-.`--' ,---. ,--,--,  /  O  \ |  .--. '|  |
 * |  ||      \|  `-,| .-. ||  .--'|        |' ,-.  |'-.  .-',--.| .-. ||      \|  .-.  ||  '--' ||  |
 * |  ||  ||  ||  .-'' '-' '|  |   |  |  |  |\ '-'  |  |  |  |  |' '-' '|  ||  ||  | |  ||  | --' |  |
 * `--'`--''--'`--'   `---' `--'   `--`--`--' `--`--'  `--'  `--' `---' `--''--'`--' `--'`--'     `--'
 */

@Plugin(id = "informationapi", name = "InformationAPI", version = BuildConstants.VERSION, description = "provide some information API", url = "bulefire.com", authors = {"bulefire_fox"})
public class InformationAPI {

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

    @Inject
    private ProxyServer server;

    @Inject
    @DataDirectory
    private Path dataDirectory;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        logger.info("[1/4]load config");
        Config.init(dataDirectory,server,container);
        logger.info("[2/4]loading Http Server");
        Init.init();
        logger.info("[3/4]loading Database");
        try {
            DataBaseInit.init();
        } catch (SQLException e){
            DatabaseUtil.handleDatabaseError(e,this.getClass());
            logger.error(e.getMessage());
            logger.error("load failed");
            return;
        }
        logger.info("[4/4]loading Command");
        CommandManager cm = server.getCommandManager();
        CommandMeta bdMeta = cm.metaBuilder("bind")
                .aliases("bd")
                .build();
        cm.register(bdMeta, new BindCommand());

        print();
        logger.info("InformationAPI v" + BuildConstants.VERSION + " is loaded!");
    }

    private void print(){
        logger.info("""
                                                                                                                   \s
                ,--.         ,---.                                  ,--.  ,--.                 ,---.  ,------. ,--.\s
                |  |,--,--, /  .-' ,---. ,--.--.,--,--,--. ,--,--.,-'  '-.`--' ,---. ,--,--,  /  O  \\ |  .--. '|  |\s
                |  ||      \\|  `-,| .-. ||  .--'|        |' ,-.  |'-.  .-',--.| .-. ||      \\|  .-.  ||  '--' ||  |\s
                |  ||  ||  ||  .-'' '-' '|  |   |  |  |  |\\ '-'  |  |  |  |  |' '-' '|  ||  ||  | |  ||  | --' |  |\s
                `--'`--''--'`--'   `---' `--'   `--`--`--' `--`--'  `--'  `--' `---' `--''--'`--' `--'`--'     `--'\s
                                                      ,---.                                            ,---.\s
                           ,---.      ,--.      ,--.  |   |    ,--.                  ,--.          ,--.|   |\s
                ,--.  ,--.'.-.  \\    /    \\    /    \\ |  .'    |  | ,---.  ,--,--. ,-|  | ,---.  ,-|  ||  .'\s
                 \\  `'  /  .-' .'   |  ()  |  |  ()  ||  |     |  || .-. |' ,-.  |' .-. || .-. :' .-. ||  | \s
                  \\    /  /   '-..--.\\    /.--.\\    / `--'     |  |' '-' '\\ '-'  |\\ `-' |\\   --.\\ `-' |`--' \s
                   `--'   '-----''--' `--' '--' `--'  .--.     `--' `---'  `--`--' `---'  `----' `---' .--. \s
                                                      '--'                                             '--' \s
                
                """);
    }
}
