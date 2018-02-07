package com.joker.registration.entity;

/**
 * @Author Joker
 * @Description 链表结构
 * @Date Create in 上午10:43 2018/2/5
 */
public class LinkNode {
    private LinkNode next;

    private Object value;

    public LinkNode(Object value) {
        this.value = value;
    }

    public LinkNode next() {
        return next;
    }

    public void next(LinkNode next) {
        this.next = next;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
