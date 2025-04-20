package com.bulefire.informationAPI.command.bind;

import com.bulefire.informationAPI.datdabase.PlayerDAO;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BindCommand implements SimpleCommand {
    private static final Logger logger = LoggerFactory.getLogger(BindCommand.class);

    private static final Result failed = new Result(false,null,null);


    private static final int MAX_ATTEMPTS = 5;
    //private static final Map<String, Integer> ATTEMPT_COUNTER = new ConcurrentHashMap<>();

    // 验证码缓存（code -> 验证信息）
    private static final ConcurrentHashMap<String, CodeInfo> CODE_CACHE = new ConcurrentHashMap<>();

    static {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // 每10分钟清理过期验证码
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            BindCommand.CODE_CACHE.entrySet().removeIf(entry ->
                    (now - entry.getValue().timestamp) > 300_000
            );
            logger.info("清理过期验证码");
        }, 10, 10, TimeUnit.MINUTES);
    }

    @Override
    public void execute(@NotNull Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (source instanceof Player player) {
            if (args.length < 1) {
                // 绑定过的不能二次绑定
                String n;
                String u;
                try {
                    n = PlayerDAO.getUserNameByUUID(player.getUniqueId());
                    u = PlayerDAO.getQIDByUUID(player.getUniqueId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (n != null && n.equals(player.getUsername()) && u != null){
                    player.sendMessage(Component.translatable("bind.message.already.bind").arguments(Component.text(u)));
                    return;
                }

                // 判断是否已经发送过验证码 60s 可以发一次
                boolean exists = CODE_CACHE.entrySet().stream()
                        .anyMatch(entry -> {
                            if (entry.getValue().username.equals(player.getUsername())) {
                                // 判断是否在60s内
                                return ((System.currentTimeMillis() - entry.getValue().timestamp) < 60_000);
                            }
                            return false;
                        });

                if (exists) {
                    player.sendMessage(Component.translatable("bind.message.already.send"));
                    return;
                }

                // 生成验证码
                UUID uuid = player.getUniqueId();

                String code = generateSecureCode();
                // 存入缓存（5分钟有效期）
                CODE_CACHE.put(code, new CodeInfo(uuid, System.currentTimeMillis(), player.getUsername()));

                logger.info("player {} get code {}", uuid, code);

                player.sendMessage(Component.translatable("bind.message.send.code").arguments(Component.text(code)));
            }
            else if (args[0].equals("search")) {
                String QID;
                try {
                    QID = PlayerDAO.getQIDByUUID(player.getUniqueId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if (QID != null) {
                    player.sendMessage(Component.translatable("bind.message.search.qq.already").arguments(Component.text(QID)));
                } else {
                    player.sendMessage(Component.translatable("bind.message.search.qq.not"));
                }
            }
            else if (args[0].equals("un")) {
                try {
                    PlayerDAO.deleteByUUID(player.getUniqueId());
                    player.sendMessage(Component.translatable("bind.message.unbind.success"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // 生成8位安全验证码
    @Contract(" -> new")
    private @NotNull String generateSecureCode() {
        logger.info("gen code");
        SecureRandom random = new SecureRandom();
        String chars = "ACDEFGHJKLMNPQRSTUVWXY3456789"; // 排除易混淆字符
        char[] code = new char[8];
        for (int i = 0; i < 8; i++) {
            code[i] = chars.charAt(random.nextInt(chars.length()));
        }
        return new String(code);
    }

    @Override
    public List<String> suggest(@NotNull Invocation invocation) {
        String[] args = invocation.arguments();

        // 第一个参数提示子命令
        if (args.length == 0) {
            return Arrays.asList("search", "un");
        }

        // 子命令的自动补全
        if (args.length == 1) {
            return Stream.of("search", "un")
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(@NotNull Invocation invocation) {
        return CompletableFuture.completedFuture(suggest(invocation));
    }

    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        return true;
        //return invocation.source().hasPermission("informationapi.blind");
    }

    // 验证信息存储类
    private static class CodeInfo{
        final UUID uuid;
        final long timestamp;
        int attempts;
        final String username;

        CodeInfo(UUID uuid, long timestamp, String username) {
            this.uuid = uuid;
            this.timestamp = timestamp;
            this.attempts = 0;
            this.username = username;
        }
    }


    public static class Result{
        public boolean valid;
        public UUID uuid;
        public String username;

        public Result(boolean valid, UUID uuid, String username) {
            this.valid = valid;
            this.uuid = uuid;
            this.username = username;
        }
    }
    @Contract("_ -> new")
    public static @NotNull Result blind(@NotNull String code) {
        CodeInfo info = BindCommand.CODE_CACHE.get(code);

        // 验证码不存在
        if (info == null)
            return failed;

        if (info.attempts >= MAX_ATTEMPTS){
            CODE_CACHE.remove(code);
            return failed;
        }

        // 检查有效期（5分钟）
        boolean valid = (System.currentTimeMillis() - info.timestamp) <= 300_000;

        if (!valid){
            info.attempts++;
            return failed;
        }

        // 无论是否通过都移除验证码（一次性使用）
        BindCommand.CODE_CACHE.remove(code);
        return new Result(true,info.uuid,info.username);
    }
}
