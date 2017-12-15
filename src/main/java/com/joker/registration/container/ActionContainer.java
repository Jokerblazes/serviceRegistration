package com.joker.registration.container;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

/**
 *
 * Created by joker on 2017/12/9.
 */
public class ActionContainer<T> {
    private static ActionContainer container = new ActionContainer();

    private ActionContainer() {}

    public static ActionContainer getContainer() {
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
