package com.joker.registration.container;

import com.joker.agreement.entity.Message;
import com.joker.registration.dto.Node;
import com.joker.registration.dto.Provider;
import com.joker.registration.entity.Storage;
import io.netty.channel.ChannelHandlerContext;



/**
 * 生产者对象
 * Created by joker on 2017/12/8.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class ProviderPO {
    private ChannelHandlerContext ctx;//channel上下文
    private Node node;//节点
    private String serviceName;//服务名

    private Storage<Message> storage;//有界缓存

    public ProviderPO(Provider provider) {
        this(provider,10);
    }
    public ProviderPO(Provider provider,int capacity) {
        this.node = provider.getNode();
        this.serviceName = provider.getServiceName();
        this.storage = new Storage<Message>(capacity);
    }


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



}
