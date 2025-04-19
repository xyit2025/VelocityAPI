package com.bulefire.informationAPI.languages;

import com.bulefire.informationAPI.config.Config;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class LocalizationManager {
    private static final Path LANG_DIR = Config.root.resolve("languages");

    public static void reloadTranslations() {
        try {
            TranslationRegistry registry = TranslationRegistry.create(Key.key("informationapi", "translations"));

            // 加载中文
            loadLocale(registry, Locale.CHINA, "messages_zh_CN.properties");
            // 加载英文
            loadLocale(registry, Locale.US, "messages_en_US.properties");

            GlobalTranslator.translator().addSource(registry);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load translations", e);
        }
    }

    private static void loadLocale(TranslationRegistry registry, Locale locale, String filename) throws Exception {
        Path file = LANG_DIR.resolve(filename);
        if (file.toFile().exists()) {
            try (FileInputStream fis = new FileInputStream(file.toFile())) {
                ResourceBundle bundle = new PropertyResourceBundle(fis);
                registry.registerAll(locale, bundle, true);
            }
        }
    }
}
