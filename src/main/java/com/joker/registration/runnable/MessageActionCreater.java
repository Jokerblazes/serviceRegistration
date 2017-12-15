package com.joker.registration.runnable;

import com.joker.agreement.entity.Message;
import com.joker.registration.entity.MessageAction;
import com.joker.registration.entity.Storage;


/**
 * Created by joker on 2017/12/12.
 */
public class MessageActionCreater implements Runnable {
    private final MessageAction messageAction;
    private final Storage<MessageAction> storage;

    public MessageActionCreater(Storage<MessageAction> storage, MessageAction messageAction) {
        this.storage = storage;
        this.messageAction = messageAction;
    }

    @Override
    public void run() {
        try {
            this.storage.put(messageAction);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
