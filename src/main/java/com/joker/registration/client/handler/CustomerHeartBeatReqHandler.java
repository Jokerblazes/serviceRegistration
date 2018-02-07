package com.joker.registration.client.handler;

import com.joker.agreement.entity.Head;
import com.joker.agreement.entity.Message;
import com.joker.agreement.entity.MessageConstant;
import com.joker.agreement.entity.MessageType;
import com.joker.registration.container.ProviderPO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 消费者心跳处理
 * Created by joker on 2017/12/12.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */

public class CustomerHeartBeatReqHandler extends SimpleChannelInboundHandler<Object> {
    private final static Logger logger = LoggerFactory.getLogger(CustomerHeartBeatReqHandler.class);

    private final ProviderPO provider;

    public CustomerHeartBeatReqHandler(ProviderPO provider) {
        this.provider = provider;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        // 握手成功，主动发送心跳消息
        if (message.getCmdType() == MessageType.Login.value() && message.getOpStatus() == MessageType.Success.value()) {
            logger.info("消费者客户端开启定时心跳任务！");
            heartBeat = ctx.executor().scheduleAtFixedRate(
                    new CustomerHeartBeatReqHandler.HeartBeatTask(ctx), 0, 80000,
                    TimeUnit.MILLISECONDS);
//            BlockingQueue<Message> queue = provider.getQueue();
            logger.info("消费者客户端开启等待服务端消息！");
//            Storage<Message> storage = provider.getStorage();
//            MessageDestroyer messageDestroyer = new MessageDestroyer(storage,ctx);
//            ctx.channel().eventLoop().parent().execute(messageDestroyer);
//            ctx.executor().next().execute(messageDestroyer);
//            ctx.executor().execute(messageDestroyer);
        } else if (message.getCmdType() == MessageType.HEARTBEAT.value() && message.getOpStatus() == MessageType.Success.value()) {
            logger.info("消费者客户端收到服务端的心跳回复消息！{}",message);
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
            logger.info("消费者客户端发送心跳消息！{}",heartBeat);
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
            logger.error("消费者客户端开始取消未执行的心跳任务！");
            heartBeat.cancel(true);
            heartBeat = null;
        }
        exceptionalProcess(ctx);
        ctx.fireExceptionCaught(cause);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("链接断开 {}",ctx.channel().toString());
        exceptionalProcess(ctx);
    }

    private void exceptionalProcess(ChannelHandlerContext ctx) {
        logger.info("从provider {}中移除对应的连接 {}",provider,ctx);
        provider.removeCtx(ctx);
    }


}
