package com.joker.registration.handler;

import com.joker.agreement.entity.Head;
import com.joker.agreement.entity.Message;
import com.joker.agreement.entity.MessageConstant;
import com.joker.agreement.entity.MessageType;
import com.joker.registration.container.ProviderPO;
import com.joker.registration.entity.Storage;
import com.joker.registration.runnable.MessageDestroyer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by joker on 2017/12/12.
 */

public class CustomerHeartBeatReqHandler extends SimpleChannelInboundHandler<Object> {
    private final ProviderPO provider;

    public CustomerHeartBeatReqHandler(ProviderPO provider) {
        this.provider = provider;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        // 握手成功，主动发送心跳消息
        if (message.getCmdType() == MessageType.Login.value() && message.getOpStatus() == MessageType.Success.value()) {
            heartBeat = ctx.executor().scheduleAtFixedRate(
                    new CustomerHeartBeatReqHandler.HeartBeatTask(ctx), 0, 80000,
                    TimeUnit.MILLISECONDS);
//            BlockingQueue<Message> queue = provider.getQueue();
            Storage<Message> storage = provider.getStorage();

            MessageDestroyer messageDestroyer = new MessageDestroyer(storage,ctx);
            ctx.channel().eventLoop().parent().execute(messageDestroyer);
//            ctx.executor().next().execute(messageDestroyer);
//            ctx.executor().execute(messageDestroyer);
        } else if (message.getCmdType() == MessageType.HEARTBEAT.value() && message.getOpStatus() == MessageType.Success.value()) {
            System.out
                    .println("CustomerClient receive server heart beat message : ---> "
                            + message);
        } else
            ctx.fireChannelRead(msg);
    }
    private volatile ScheduledFuture<?> heartBeat;


    private class HeartBeatTask implements Runnable {
        private final ChannelHandlerContext ctx;

        public HeartBeatTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void run() {
            Message heatBeat = buildHeatBeat();
            System.out
                    .println("CustomerClient send heart beat messsage to server : ---> "
                            + heatBeat);
            ctx.writeAndFlush(heatBeat);
        }

        private Message buildHeatBeat() {
            Message message = new Message();
            Head head = new Head();
            head.setHead(MessageConstant.Header);
            head.setLength(0);
            message.setHead(head);;
            message.setCmdType(MessageType.HEARTBEAT.value());
            message.setLength(18+head.getLength());
            return message;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }

}
