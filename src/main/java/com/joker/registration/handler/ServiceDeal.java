package com.joker.registration.handler;

import com.joker.agreement.entity.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by joker on 2017/12/10.
 */
public interface ServiceDeal {

    Message handleAndReturnMessage(Message message,ChannelHandlerContext ctx);
}
