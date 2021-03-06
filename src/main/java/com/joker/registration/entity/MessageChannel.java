package com.joker.registration.entity;

import com.joker.agreement.entity.Message;
import io.netty.channel.ChannelHandlerContext;
import rx.functions.Action1;

/**
 * 存储channel上下文和消息
 * Created by joker on 2017/12/12.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class MessageChannel {
//    private final Action1 action1;
//    private final Message message;
//
//    public MessageAction(Action1 action1, Message message) {
//        this.action1 = action1;
//        this.message = message;
//    }
//
//    public Action1 getAction1() {
//        return action1;
//    }
//
//    public Message getMessage() {
//        return message;
//    }

    private final ChannelHandlerContext ctx;
    private final Message message;

    public MessageChannel(ChannelHandlerContext ctx, Message message) {
        this.ctx = ctx;
        this.message = message;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public Message getMessage() {
        return message;
    }
}
