package com.bulefire.informationAPI.api;

import com.bulefire.informationAPI.api.body.*;
import com.bulefire.informationAPI.api.result.FindPlayerResult;
import com.bulefire.informationAPI.config.Config;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Init {
    private static final Logger logger = LoggerFactory.getLogger(Init.class);

    private static final Map<String,Object> UnauthorizedErr = new HashMap<>();
    private static final Map<String,Object> UnsupportedMediaTypeErr = new HashMap<>();

    static {
        UnauthorizedErr.put("timestamp", System.currentTimeMillis());
        UnauthorizedErr.put("status", 401);
        UnauthorizedErr.put("error", "Unauthorized");
        UnauthorizedErr.put("message", "Invalid or missing API token");

        UnsupportedMediaTypeErr.put("error", "Unsupported Media Type");
        UnsupportedMediaTypeErr.put("message", "Content-Type must be application/json");
    }

    public static void init(){
        String base = Config.getConfigJson().getAddress().getBase();
        String query = Config.getConfigJson().getAddress().getQuery();
        String find_player = Config.getConfigJson().getAddress().getFind_player();
        String hh = Config.getConfigJson().getAddress().getHh();
        String blind = Config.getConfigJson().getAddress().getBlind();

        // 初始化HttpServer
        logger.info("正在初始化HttpServer...");
        // 创建Javalin实例
        HttpServer.app = Javalin.create().start(Config.getConfigJson().getPort());

        // 一点用都没有
        // 注册过滤器
        HttpServer.app.before("/*",ctx -> {
            logger.info("hader");
            if (!"application/json".equals(ctx.contentType())) {
                logger.info("请求被拒绝，原因：Unsupported Media Type");
                ctx.status(415).json(UnsupportedMediaTypeErr).result();
                return;
            }
           String token = ctx.header("Authorization");

            List<String> validTokens = Config.getConfigJson().getToken();
            if (token == null || !validTokens.contains(token)){
                ctx.status(401).json(UnauthorizedErr).result();
                return;
            }
            logger.info("pass");
        });

        // 注册接口
        // query
        HttpServer.app.post(base+query, ctx -> {
            logger.info("query called");
            if (!"application/json".equals(ctx.contentType())) {
                ctx.status(415).json(UnsupportedMediaTypeErr);
                logger.info("query 请求被拒绝，原因：Unsupported Media Type");
                return; // 明确终止请求
            }

            String token = ctx.header("Authorization");
            List<String> validTokens = Config.getConfigJson().getToken();
            if (token == null || !validTokens.contains(token)){
                ctx.status(401).json(UnauthorizedErr);
                logger.warn("query 请求被拒绝，原因：Invalid or missing API token");
                return;
            }

            Gson g = new Gson();
            QueryBody q = g.fromJson(ctx.body(), QueryBody.class);
            QueryReturn result = HttpServer.query(q.getServer(), q.getPage());
            if (result == null){
                logger.warn("query 请求被拒绝，原因：page < 0");
                ctx.status(HttpStatus.BAD_REQUEST).result("page must be > 0 instead < 0");
                return;
            }
            logger.info("query result is: {} {}", result.getPlayer_number(), result.getPlayers());
            ctx.status(200).json(result);
        });
        // find_player
        HttpServer.app.post(base+find_player, ctx -> {
            logger.info("find_player called");
            if (!"application/json".equals(ctx.contentType())) {
                ctx.status(415).json(UnsupportedMediaTypeErr);
                logger.info("find_player 请求被拒绝，原因：Unsupported Media Type");
                return; // 明确终止请求
            }

            String token = ctx.header("Authorization");
            List<String> validTokens = Config.getConfigJson().getToken();
            if (token == null || !validTokens.contains(token)){
                ctx.status(401).json(UnauthorizedErr);
                logger.warn("find_player 请求被拒绝，原因：Invalid or missing API token");
                return;
            }

            ctx.body();
            Gson g = new Gson();
            FindPlayerResult result = HttpServer.find_player(g.fromJson(ctx.body(), FindPlayerBody.class).getName());
            logger.info("find_player result is: {}", result);
            ctx.status(200).json(result);
        });
        // hh
        HttpServer.app.post(base+hh, ctx -> {
            logger.info("hh called");
            if (!"application/json".equals(ctx.contentType())) {
                ctx.status(415).json(UnsupportedMediaTypeErr);
                logger.info("hh 请求被拒绝，原因：Unsupported Media Type");
                return; // 明确终止请求
            }

            String token = ctx.header("Authorization");
            List<String> validTokens = Config.getConfigJson().getToken();
            if (token == null || !validTokens.contains(token)){
                ctx.status(401).json(UnauthorizedErr);
                logger.warn("hh 请求被拒绝，原因：Invalid or missing API token");
                return;
            }
            Gson g = new Gson();
            HhBody b = g.fromJson(ctx.body(), HhBody.class);
            String result = HttpServer.hh(b.getqID(),b.getMessage());
            logger.info("hh result is: {}", result);
            ctx.status(Integer.parseInt(result));
        });

        // blind
        HttpServer.app.post(base+blind, ctx -> {
            logger.info("blind called");
            if (!"application/json".equals(ctx.contentType())) {
                ctx.status(415).json(UnsupportedMediaTypeErr);
                logger.info("blind 请求被拒绝，原因：Unsupported Media Type");
                return;
            }

            String token = ctx.header("Authorization");
            List<String> validTokens = Config.getConfigJson().getToken();
            if (token == null || !validTokens.contains(token)){
                ctx.status(401).json(UnauthorizedErr);
                logger.warn("blind 请求被拒绝，原因：Invalid or missing API token");
                return;
            }

            Gson g = new Gson();
            BlindBody b = g.fromJson(ctx.body(), BlindBody.class);
            boolean result =  HttpServer.blind(b.getQqID(),b.getCode());
            if (result)
                ctx.status(200);
            else
                ctx.status(403);
        });
    }


}
