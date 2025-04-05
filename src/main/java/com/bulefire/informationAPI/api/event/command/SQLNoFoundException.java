package com.bulefire.informationAPI.api.event.command;

import java.sql.SQLException;

public class SQLNoFoundException extends SQLException {
    public SQLNoFoundException(String message) {
        super(message);
    }
}
