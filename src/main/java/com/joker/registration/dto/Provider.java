package com.joker.registration.dto;

import org.msgpack.annotation.Message;

/**
 * Created by joker on 2017/12/7.
 */
@Message
public class Provider {
    private Node node;
    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
