package com.joker.registration.runnable;

import com.joker.registration.entity.MessageChannel;
import com.joker.registration.entity.Storage;


/**
 * 生产者-生成messageChannel放入缓存
 * Created by joker on 2017/12/12.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class MessageChannelCreater implements Runnable {
    private final MessageChannel messageChannel;
    private final Storage<MessageChannel> storage;

    public MessageChannelCreater(Storage<MessageChannel> storage, MessageChannel messageChannel) {
        this.storage = storage;
        this.messageChannel = messageChannel;
    }

    @Override
    public void run() {
        try {
            this.storage.put(messageChannel);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
