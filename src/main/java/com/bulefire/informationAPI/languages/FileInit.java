package com.bulefire.informationAPI.languages;

import com.bulefire.informationAPI.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileInit {
    private static final Logger logger = LoggerFactory.getLogger(FileInit.class);

    public static void init() {
        Path p = Config.root.resolve("languages");
        if (!Files.exists(p)){
            try {
                Files.createDirectory(p);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}
