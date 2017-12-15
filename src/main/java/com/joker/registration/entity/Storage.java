package com.joker.registration.entity;



/**
 * Created by joker on 2017/12/12.
 */
public class Storage<T> extends BaseBoundedBuffer<T> {

    public Storage(int capacity) {
        super(capacity);
    }

    public synchronized void put(T t) throws InterruptedException {
        while (isFull())
            wait();
        doPut(t);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (isEmpty())
            wait();
        T t = doTake();
        notifyAll();
        return t;
    }

}
