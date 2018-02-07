package com.joker.registration.utils;

import com.joker.agreement.entity.Message;

import java.io.UnsupportedEncodingException;

/**
 * @Author Joker
 * @Description
 * @Date Create in 下午4:11 2018/2/5
 */
public class UriUtils {
    private UriUtils() {}

    public static String getStringFromMessage(final Message message) {
        String uri = null;
        try {
            uri = new String(message.getHead().getUrl(),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return uri + "," + message.getSource() + "," + message.getDest();
    }
}
