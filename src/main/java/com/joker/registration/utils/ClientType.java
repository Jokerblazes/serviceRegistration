package com.joker.registration.utils;

/**
 * Created by joker on 2017/12/8.
 */
public enum ClientType {
    CUSTOMER(1),
    PROVIDE(2);

    private int value;

    private ClientType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
