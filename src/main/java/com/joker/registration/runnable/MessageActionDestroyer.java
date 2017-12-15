package com.joker.registration.runnable;

import com.joker.agreement.entity.Message;
import com.joker.registration.entity.MessageAction;
import com.joker.registration.entity.Storage;
import io.netty.channel.ChannelHandlerContext;
import rx.Observable;
import rx.functions.Action1;


/**
 * Created by joker on 2017/12/12.
 */
public class MessageActionDestroyer implements Runnable {
    private final Storage<MessageAction> storage;

    public MessageActionDestroyer(Storage<MessageAction> storage) {
        this.storage = storage;
    }


    @Override
    public void run() {
        MessageAction messageAction = null;
        try {
            messageAction = storage.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (messageAction != null) {
            ChannelHandlerContext ctx = messageAction.getCtx();
            Message message = messageAction.getMessage();
            ctx.writeAndFlush(message);
        }
    }
}
