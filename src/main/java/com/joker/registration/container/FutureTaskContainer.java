package com.joker.registration.container;


import com.joker.registration.common.timeout.FutureTaskDecorator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author Joker
 * @Description
 * Key是由uri + 客户端连接编号 + 自动编号生成器生成的一段编号
 * @Date Create in 下午2:10 2018/2/5
 */
public class FutureTaskContainer {
//    private final static ActionContainer container = new ActionContainer();
//
//    private ActionContainer() {}
//
//    public ActionContainer getInstance() {
//        return container;
//    }

//    MessageCallable callable = futureTaskDecorator.getCallable();
//        callable.setMessage(new Message());
//        futureTask.run();


    private ConcurrentMap<String,FutureTaskDecorator> futureMap = new ConcurrentHashMap<>();


    public FutureTaskDecorator get(String key) {
        return futureMap.get(key);
    }

    public FutureTaskDecorator getAndRemove(String key) {
        return futureMap.remove(key);
    }

    public FutureTaskDecorator put(String key,FutureTaskDecorator action1) {
        return futureMap.putIfAbsent(key,action1);
    }




}
