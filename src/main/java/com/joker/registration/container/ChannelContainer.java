package com.joker.registration.container;


import java.util.concurrent.*;

/**
 * Channel容器类
 * Created by joker on 2017/12/9.
 */
public class ChannelContainer<T> {
    private static ChannelContainer container = new ChannelContainer();

    private ChannelContainer() {}

    public static ChannelContainer getContainer() {
        return container;
    }

    private ConcurrentMap<String,T> map = new ConcurrentHashMap<String, T>();

    public void put(String key,T value) {
        map.putIfAbsent(key,value);
    }

    public T get(String key) {
        return map.remove(key);
    }

}
