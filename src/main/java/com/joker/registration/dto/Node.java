package com.joker.registration.dto;


import org.msgpack.annotation.Message;

/**
 * Created by joker on 2017/12/7.
 */
@Message
public class Node {
    private int id;
    private String ip;
    private int port;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
