package com.joker.registration.runnable;

import com.joker.agreement.entity.Message;
import com.joker.registration.entity.Storage;
import io.netty.channel.ChannelHandlerContext;


/**
 * Created by joker on 2017/12/12.
 * 消费者-作为Message的消费者
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class MessageDestroyer implements Runnable {
    private final ChannelHandlerContext ctx;
    private final Storage<Message> storage;

    public MessageDestroyer(Storage<Message> storage, ChannelHandlerContext ctx) {
        this.storage = storage;
        this.ctx = ctx;
    }


    @Override
    public void run() {
        while (true) {
            System.out.println("MessageDestroyerThread:"+Thread.currentThread());
            Message message = null;
            try {
                message = storage.take();
            } catch (InterruptedException e) {
                System.out.println("MessageDestroyerThread is destory");
                Thread.currentThread().interrupt();
            }

            if (message != null)
                ctx.writeAndFlush(message);
        }
    }
}
