package com.joker.registration.container;

import com.joker.agreement.entity.Message;
import com.joker.registration.dto.Node;
import com.joker.registration.dto.Provider;
import com.joker.registration.entity.Storage;
import io.netty.channel.ChannelHandlerContext;



/**
 * Created by joker on 2017/12/8.
 */
public class ProviderPO {
    public ProviderPO(Provider provider) {
        this.node = provider.getNode();
        this.serviceName = provider.getServiceName();
        this.storage = new Storage<Message>(10);
//        this.queue = messageQueue;
    }


    private ChannelHandlerContext ctx;
    private Node node;
    private String serviceName;
//    private volatile BlockingQueue<Message> queue;
    private Storage<Message> storage;

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

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public Storage<Message> getStorage() {
        return storage;
    }

    public void setStorage(Storage<Message> storage) {
        this.storage = storage;
    }

    //    public BlockingQueue<Message> getQueue() {
//        return queue;
//    }


}
