package com.bulefire.informationAPI.languages;

import com.bulefire.informationAPI.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileInit {
    private static final Logger logger = LoggerFactory.getLogger(FileInit.class);

    public static void init() {
        logger.info("init languages folder");
        Path p = Config.root.resolve("languages");
        logger.info("languages folder path: {}", p);
        logger.info(p.toAbsolutePath().toString());
        if (!Files.exists(p)){
            logger.info("languages folder not found, creating...");
            try {
                Files.createDirectories(p);
                logger.info("languages folder init success");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        if (!Files.exists(p.resolve("messages_zh_CN.properties"))){
            logger.info("messages_zh_CN.properties not found, creating...");
            try {
                Path file = p.resolve("messages_zh_CN.properties");
                Files.writeString(file,
                        DEFAULT_ZH_CONTENT,
                        StandardOpenOption.CREATE);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        if (!Files.exists(p.resolve("messages_en_US.properties"))){
            logger.info("messages_en_US.properties not found, creating...");
            try {
                Path file = p.resolve("messages_en_US.properties");
                Files.writeString(file,
                        DEFAULT_US_CONTENT,
                        StandardOpenOption.CREATE);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        LocalizationManager.reloadTranslations();
    }

    private static final String DEFAULT_ZH_CONTENT = """
            hh.server_show.format = [来自Q群]<{0}> {1}
            
            bind.message.success = 验证成功
            bind.message.already.bind = 您已经绑定了 {0}
            bind.message.already.send = 已经发送过了
            bind.message.send.code = 您的验证码是 {0} \\n 有效期5分钟,请前往qq端验证
            bind.message.search.qq.already = 已绑定 {0}
            bind.message.search.qq.not = 未绑定 QQ
            bind.message.unbind.success = 解绑成功
            
            shout.help.message = Usage: /shout add/set/find <name> [number]
            shout.add.help.message = Usage: /shout add <name> <shout>
            shout.add.successful = 成功添加 {0} shout 到账户 {1}
            shout.add.successful.update = 新的 shout 是 {0}
            shout.find.bind.qq.not = 您还没有绑定过 QQ
            shout.find.message = 你的 shout 是 {0}
            shout.set.help.message = /set <name> <number>
            shout.set.error.negative = shout 不能为负!
            shout.set.successful = 成功设置 {0} 的 shout 为 {1}
            """;

    private static final String DEFAULT_US_CONTENT = """
            hh.server_show.format = [来自Q群]<{0}> {1}
            
            bind.message.success = 验证成功
            bind.message.already.bind = 您已经绑定了 {0}
            bind.message.already.send = 已经发送过了
            bind.message.send.code = 您的验证码是 {0} \\n 有效期5分钟,请前往qq端验证
            bind.message.search.qq.already = 已绑定 {0}
            bind.message.search.qq.not = 未绑定 QQ
            bind.message.unbind.success = 解绑成功
            
            shout.help.message = Usage: /shout add/set/find <name> [number]
            shout.add.help.message = Usage: /shout add <name> <shout>
            shout.add.successful = 成功添加 {0} shout 到账户 {1}
            shout.add.successful.update = 新的 shout 是 {0}
            shout.find.bind.qq.not = 您还没有绑定过 QQ
            shout.find.message = 你的 shout 是 {0}
            shout.set.help.message = /set <name> <number>
            shout.set.error.negative = shout 不能为负!
            shout.set.successful = 成功设置 {0} 的 shout 为 {1}
            """;
}
