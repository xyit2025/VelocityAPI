package com.bulefire.informationAPI.config;

import java.util.List;

public class ConfigJson {
    private Integer port;
    private List<String> token;
    private Address address;
    private Database database;
    private String format;

    public static class Address {
        private String base;
        private String query;
        private String find_player;
        private String hh;
        private String blind;

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getFind_player() {
            return find_player;
        }

        public void setFind_player(String find_player) {
            this.find_player = find_player;
        }

        public String getHh() {
            return hh;
        }

        public void setHh(String hh) {
            this.hh = hh;
        }

        public String getBlind() {
            return blind;
        }

        public void setBlind(String blind) {
            this.blind = blind;
        }
    }

    public static class Database{
        private String url;
        private String username;
        private String password;
        private Integer poolSize;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Integer getPoolSize() {
            return poolSize;
        }

        public void setPoolSize(Integer poolSize) {
            this.poolSize = poolSize;
        }
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database dadaBase) {
        this.database = dadaBase;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<String> getToken() {
        return token;
    }

    public void setToken(List<String> token) {
        this.token = token;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
