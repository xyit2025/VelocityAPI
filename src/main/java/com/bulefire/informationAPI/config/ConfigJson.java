package com.bulefire.informationAPI.config;

import java.util.List;

public class ConfigJson {
    private Integer port;
    private List<String> token;
    private Address address;

    public static class Address {
        private String base;
        private String query;
        private String find_player;
        private String hh;

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
}
