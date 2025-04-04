package com.bulefire.informationAPI.api.event.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class BlindCommand implements SimpleCommand {
    private static final Logger logger = LoggerFactory.getLogger(BlindCommand.class);

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
            BlindCommand.CODE_CACHE.entrySet().removeIf(entry ->
                    (now - entry.getValue().timestamp) > 300_000
            );
            logger.info("清理过期验证码");
        }, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * Executes the command for the specified invocation.
     *
     * @param invocation the invocation context
     */
    @Override
    public void execute(@NotNull Invocation invocation) {
        CommandSource source = invocation.source();
        //String[] args = invocation.arguments();

        if (source instanceof Player player){
            UUID uuid = player.getUniqueId();

            String code = generateSecureCode();
            // 存入缓存（5分钟有效期）
            CODE_CACHE.put(code, new CodeInfo(uuid, System.currentTimeMillis(),player.getUsername()));

            logger.info("player {} get code {}",uuid,code);

            player.sendMessage(Component.text("您的验证码是" + code +"\n 有效期5分钟,请前往qq端验证"));
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

    /**
     * Provides tab complete suggestions for the specified invocation.
     *
     * @param invocation the invocation context
     * @return the tab complete suggestions
     */
    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }

    /**
     * Provides tab complete suggestions for the specified invocation.
     *
     * @param invocation the invocation context
     * @return the tab complete suggestions
     * @implSpec defaults to wrapping the value returned by {@link #suggest(Invocation)}
     */
    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return SimpleCommand.super.suggestAsync(invocation);
    }

    /**
     * Tests to check if the source has permission to perform the specified invocation.
     *
     * <p>If the method returns {@code false}, the handling is forwarded onto
     * the players current server.
     *
     * @param invocation the invocation context
     * @return {@code true} if the source has permission
     */
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
        CodeInfo info = BlindCommand.CODE_CACHE.get(code);

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
        BlindCommand.CODE_CACHE.remove(code);
        return new Result(true,info.uuid,info.username);
    }
}
