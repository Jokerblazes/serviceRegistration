//package com.joker.registration.entity;
//
//
//
///**
// * 有界缓存
// * Created by joker on 2017/12/12.
// * https://github.com/Jokerblazes/serviceRegistration.git
// */
//public class Storage<T> extends BaseBoundedBuffer<T> {
//
//    public Storage() {
//        this(10);
//    }
//
//    public Storage(int capacity) {
//        super(capacity);
//    }
//
//    public synchronized void put(T t) throws InterruptedException {
//        while (isFull())
//            wait();
//        doPut(t);
//        notifyAll();
//    }
//
//    public synchronized T take() throws InterruptedException {
//        while (isEmpty())
//            wait();
//        T t = doTake();
//        notifyAll();
//        return t;
//    }
//
//}
