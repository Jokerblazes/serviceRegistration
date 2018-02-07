package com.joker.registration.common.timeout;


import com.joker.agreement.entity.Message;

import java.util.concurrent.Callable;

/**
 * @Author Joker
 * @Description
 * @Date Create in 下午2:39 2017/12/29
 */
public class MessageCallable implements Callable<Message> {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public Message call() throws Exception {
        return message;
    }
}
