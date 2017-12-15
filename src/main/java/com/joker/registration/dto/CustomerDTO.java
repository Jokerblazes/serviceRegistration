package com.joker.registration.dto;


import org.msgpack.annotation.Message;

import java.util.List;

/**
 * Created by joker on 2017/12/7.
 */
@Message
public class CustomerDTO {

    private Node node;

    private List<String> serviceNames;

    public List<String> getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(List<String> serviceNames) {
        this.serviceNames = serviceNames;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }


}
