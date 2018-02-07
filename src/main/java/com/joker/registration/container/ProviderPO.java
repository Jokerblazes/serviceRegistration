package com.joker.registration.container;

import com.joker.registration.dto.Node;
import com.joker.registration.dto.ProviderDTO;
import com.joker.registration.entity.LinkNode;
import com.joker.registration.utils.RandomUtils;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;


/**
 * 生产者对象
 * Created by joker on 2017/12/8.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class ProviderPO {
//    private ChannelHandlerContext ctx;//channel上下文
    private Node node;//节点
    private String serviceName;//服务名
    private int capacity;//允许多少个连接数
    private FutureTaskContainer container;
    private ChannelHandlerContext[] ctxs;
    private LinkNode[] nodes;
    private Object[] locks;
    private final int N_LOCK = 16;
//    private Storage<Message> storage;//有界缓存

    public ProviderPO(ProviderDTO provider) {
        this(provider,10);
    }
    public ProviderPO(ProviderDTO provider,int capacity) {
        this.node = provider.getNode();
        this.serviceName = provider.getServiceName();
        this.capacity = capacity;
        ctxs = new ChannelHandlerContext[capacity];
        nodes = new LinkNode[capacity];
        locks = new Object[N_LOCK];
        container = new FutureTaskContainer();
//        this.storage = new Storage<Message>(capacity);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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

    private final int hash(Object key) {
        return Math.abs(key.hashCode() % capacity);
    }

    /**
     * 添加连接上下文
     * @param ctx
     * @return
     */
    public boolean addCtx(ChannelHandlerContext ctx) {
        int hash = hash(ctx);
        synchronized (locks[hash % N_LOCK]) {
            LinkNode parent = null;
            for (LinkNode node = nodes[hash]; node != null ; node.next())
                parent = node;
            if (parent == null)
                nodes[hash] = new LinkNode(ctx);
            else
                parent.next(new LinkNode(ctx));
            return true;
        }
    }

    /**
     * 移除连接上下文
     * @param ctx
     */
    public boolean removeCtx(ChannelHandlerContext ctx) {
        int hash = hash(ctx);
        synchronized (locks[hash % N_LOCK]) {
            LinkNode parent = nodes[hash];
            for (LinkNode node = nodes[hash]; node != null ; node.next()) {
                if (node.getValue().equals(ctx)) {
                    parent.next(node.next());
                    return true;
                }
                parent = node;
            }
        }
        return false;
    }

    /**
     * 获取连接
     * @return
     * 提供的是随机获取机制
     */
    public ChannelHandlerContext getCtx() {
        int hash = RandomUtils.getRandomFromRange(capacity);
        synchronized (locks[hash % N_LOCK]) {
            List<LinkNode> linkNodes = new ArrayList<>();
            for (LinkNode node = nodes[hash]; node != null ; node.next())
                linkNodes.add(node);
            int i = RandomUtils.getRandomFromRange(linkNodes.size());
            return (ChannelHandlerContext) linkNodes.get(i).getValue();
        }
    }

    public FutureTaskContainer getContainer() {
        return container;
    }

//    public ChannelHandlerContext getCtx() {
//        return ctx;
//    }
//
//    public void setCtx(ChannelHandlerContext ctx) {
//        this.ctx = ctx;
//    }

//    public Storage<Message> getStorage() {
//        return storage;
//    }
//
//    public void setStorage(Storage<Message> storage) {
//        this.storage = storage;
//    }

}
