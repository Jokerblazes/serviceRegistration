package com.joker.registration.utils;


import io.netty.util.AttributeKey;

/**
 * Created by joker on 2017/12/9.
 */
public class Constent {
    private Constent() {}

    public static final String ID = "&id=";

    public static AttributeKey<Object> ATTACHMENT_KEY = AttributeKey.valueOf("ATTACHMENT_KEY");
}
