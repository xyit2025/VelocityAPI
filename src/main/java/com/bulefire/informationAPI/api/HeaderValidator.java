package com.bulefire.informationAPI.api;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class HeaderValidator {
    public static void checkContentType(@NotNull Context ctx, @NotNull String expectedType) {
        String contentType = ctx.header("Content-Type");
        if (contentType == null || !contentType.equalsIgnoreCase(expectedType)) {
            ctx.status(415).json(Map.of(
                    "error", "Unsupported Media Type",
                    "required", expectedType
            ));
        }
    }
}
