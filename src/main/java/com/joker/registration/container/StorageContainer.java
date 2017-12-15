package com.joker.registration.container;

import com.joker.registration.entity.Storage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 有界缓存容器
 * Created by joker on 2017/12/12.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class StorageContainer {
    private static StorageContainer container = new StorageContainer();

    private StorageContainer() {}

    public static StorageContainer getInstance() {
        return container;
    }

    private ConcurrentMap<Integer,Storage> map = new ConcurrentHashMap<>();


    public void put(Integer key,Storage storage) {
        map.putIfAbsent(key,storage);
    }

    public Storage get(Integer key) {
        return map.get(key);
    }


    public void remove(Integer key) {
        map.remove(key);
    }





}
