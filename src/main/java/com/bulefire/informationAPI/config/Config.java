package com.bulefire.informationAPI.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public static ProxyServer server;
    public static PluginContainer container;

    private static Path configFile;
    private static ConfigJson configJson;

    public static void init(@NotNull Path p, @NotNull ProxyServer servers, @NotNull PluginContainer containers) throws IOException {
        server = servers;
        container = containers;

        configJson = new ConfigJson();
        configFile = p.resolve("config.json");

        logger.info("config directory: {}", p);
        logger.info("config file: {}", configFile);

        // 检测配置文件是否存在
        // 目录
        if (!Files.exists(p)){
            logger.info("config directory is not exists, creating ...");
            try {
                Files.createDirectory(p);
            }catch (FileAlreadyExistsException e){
                logger.warn(String.valueOf(e));
            }
        }
        // 文件
        if (!Files.exists(configFile)){
            logger.info("config file is not exists");
            logger.info("init config file");
            // 初始化配置文件内容
            configJson.setPort(8080);
            configJson.setToken(new ArrayList<>(){});
            configJson.setFormat("[来自Q群]<%username%>%message%");

            ConfigJson.Address address = new ConfigJson.Address();
            address.setBase("/vc");
            address.setQuery("/query");
            address.setFind_player("/find_player");
            address.setHh("/hh");
            address.setBlind("/blind");
            configJson.setAddress(address);

            ConfigJson.Database database = new ConfigJson.Database();
            database.setUrl("jdbc:mysql://localhost:3306/player_db?serverTimezone=Asia/Shanghai");
            database.setUsername("root");
            database.setPassword("123456");
            database.setPoolSize(10);
            configJson.setDatabase(database);


            save();
        }else{
            loadFile();
        }

        printInformation();
    }

    public static ConfigJson getConfigJson() {
        return configJson;
    }

    private static void save() {
        logger.info("save config file...");
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(configFile.toFile().getPath())){
            logger.info("save ConfigFile to file");
            g.toJson(configJson, writer);
        }catch (IOException e){
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    private static void loadFile() {
        logger.info("load config file...");
        Gson g = new Gson();
        try(FileReader reader = new FileReader(configFile.toFile().getPath())){
            configJson = g.fromJson(reader, ConfigJson.class);
        } catch (IOException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    private static void printInformation(){
        logger.info("""
                    some information :
                    
                    listening on 0.0.0.0:{}
                    
                    allow token :
                    {}
                    
                    base url    \t: \t{}
                    query       \t: \t{}
                    find_player \t: \t{}
                    hh          \t: \t{}
                    """, configJson.getPort(),
                configJson.getToken(),
                configJson.getAddress().getBase(),
                configJson.getAddress().getQuery(),
                configJson.getAddress().getFind_player(),
                configJson.getAddress().getHh()
        );
    }
}
