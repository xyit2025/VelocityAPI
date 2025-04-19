package com.bulefire.informationAPI.datdabase;

import java.sql.SQLException;

public class SQLNoFoundException extends SQLException {
    public SQLNoFoundException(String message) {
        super(message);
    }
}
