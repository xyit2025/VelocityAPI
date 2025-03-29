package com.bulefire.informationAPI.api;

import com.bulefire.informationAPI.api.body.FindPlayerBody;
import com.bulefire.informationAPI.api.body.HhBody;
import com.bulefire.informationAPI.api.result.FindPlayerResult;
import com.bulefire.informationAPI.config.Config;
import com.google.gson.Gson;
import io.javalin.Javalin;
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

        // 初始化HttpServer
        logger.info("正在初始化HttpServer...");
        // 创建Javalin实例
        HttpServer.app = Javalin.create().start(Config.getConfigJson().getPort());

        // 一点用都没有
        // 注册过滤器
//        HttpServer.app.before(base+"/*",ctx -> {
//            logger.info("hader");
//            if (!"application/json".equals(ctx.contentType())) {
//                Map<String, Object> err = new HashMap<>();
//                err.put("error", "Unsupported Media Type");
//                err.put("message", "Content-Type must be application/json");
//                ctx.status(415).json(err);
//                logger.info("请求被拒绝，原因：Unsupported Media Type");
//                return; // 明确终止请求
//            }
//           String token = ctx.header("Authorization");
//
//            List<String> validTokens = Config.getConfigJson().getToken();
//            if (token == null || !validTokens.contains(token)){
//                Map<String, Object> err = new HashMap<>();
//                err.put("timestamp", System.currentTimeMillis());
//                err.put("status", 401);
//                err.put("error", "Unauthorized");
//                err.put("message", "Invalid or missing API token");
//
//                ctx.status(401).json(err);
//                logger.warn("请求被拒绝，原因：Invalid or missing API token");
//                return;
//            }
//            logger.info("pass");
//        });

        // 注册接口
        HttpServer.app.get(base+query, ctx -> {
            logger.info("query called");
            String token = ctx.header("Authorization");
            List<String> validTokens = Config.getConfigJson().getToken();
            if (token == null || !validTokens.contains(token)){
                ctx.status(401).json(UnauthorizedErr);
                logger.warn("query 请求被拒绝，原因：Invalid or missing API token");
                return;
            }
            String result = HttpServer.query();
            logger.info("query result is: {}", result);
            ctx.status(200).json(result);
        });

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
            HttpServer.hh(g.fromJson(ctx.body(), HhBody.class).getMessage());
            logger.info("hh result is: {}", "200");
            ctx.status(200);
        });
    }


}
