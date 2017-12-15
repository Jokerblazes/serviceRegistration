package com.joker.registration.runnable;

import com.joker.agreement.entity.Message;
import com.joker.registration.entity.Storage;


/**
 * Created by joker on 2017/12/12.
 */
public class MessageCreater implements Runnable {
    private final Message message;
    private final Storage<Message> storage;

    public MessageCreater(Storage<Message> storage, Message message) {
        this.storage = storage;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            this.storage.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
