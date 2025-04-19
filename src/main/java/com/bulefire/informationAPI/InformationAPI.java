package com.bulefire.informationAPI;

import com.bulefire.informationAPI.api.Init;
import com.bulefire.informationAPI.command.Register;
import com.bulefire.informationAPI.datdabase.DataBaseInit;
import com.bulefire.informationAPI.config.Config;
import com.bulefire.informationAPI.datdabase.DatabaseUtil;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
    public Path dataDirectory;

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
        Register.register(server);

        print();
        t();
        logger.info("InformationAPI v" + BuildConstants.VERSION + " is loaded!");
    }

    public static void  t(){
        GlobalTranslator registry = GlobalTranslator.translator();
        TranslationRegistry r = TranslationRegistry.create(Key.key("informationapi"));
        Map<String, MessageFormat> translations = new HashMap<>();
        translations.put("informationapi.bind", new MessageFormat("bind!!!!!!!! {0}"));
        r.registerAll(Locale.CHINA,translations);
        registry.addSource(r);
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
