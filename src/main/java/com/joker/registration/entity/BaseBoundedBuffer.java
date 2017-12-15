package com.joker.registration.entity;

/**
 * 有界缓存基类
 * Created by joker on 2017/12/12.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public abstract class BaseBoundedBuffer<V> {
    private final V[] buf;//缓存数组
    private int tail;//尾指针
    private int head;//头指针
    private int count;//计数器

    public BaseBoundedBuffer() {
        this(10);
    }

    public BaseBoundedBuffer(int capacity) {
        this.buf = (V[])new Object[capacity];
    }

    protected synchronized final void doPut(V v) {
        buf[tail] = v;
        if (++ tail == buf.length)
            tail = 0;
        ++count;
    }

    protected synchronized final V doTake() {
        V v = buf[head];
        buf[head] = null;
        if (++head == buf.length)
            head = 0;
        --count;
        return v;
    }

    public synchronized final boolean isFull() {
        return count == buf.length;
    }

    public synchronized final boolean isEmpty() {
        return count == 0;
    }


}
