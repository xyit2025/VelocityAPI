package com.bulefire.informationAPI.api.result;

public class FindPlayerResult {
    private Boolean isOnline;
    private String server;

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
