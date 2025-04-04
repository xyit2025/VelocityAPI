package com.bulefire.informationAPI.api.event.command;

public class SQLNoFoundException extends RuntimeException {
    public SQLNoFoundException(String message) {
        super(message);
    }
}
