package com.joker.registration.runnable;

import com.joker.agreement.entity.Message;
import com.joker.registration.entity.MessageChannel;
import com.joker.registration.entity.Storage;
import io.netty.channel.ChannelHandlerContext;


/**
 * 消费者-作为MessageChannel的消费者
 * Created by joker on 2017/12/12.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class MessageChannelDestroyer implements Runnable {
    private final Storage<MessageChannel> storage;

    public MessageChannelDestroyer(Storage<MessageChannel> storage) {
        this.storage = storage;
    }


    @Override
    public void run() {
        MessageChannel messageAction = null;
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
