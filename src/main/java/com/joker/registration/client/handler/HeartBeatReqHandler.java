package com.joker.registration.client.handler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


import com.joker.agreement.entity.Head;
import com.joker.agreement.entity.Message;
import com.joker.agreement.entity.MessageConstant;
import com.joker.agreement.entity.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注册客户端心跳处理
 * Created by joker on 2017/12/12.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class HeartBeatReqHandler extends SimpleChannelInboundHandler<Object> {
	private final static Logger logger = LoggerFactory.getLogger(HeartBeatReqHandler.class);
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		Message message = (Message) msg;
		// 握手成功，主动发送心跳消息
		if (message.getCmdType() == MessageType.Login.value() && message.getOpStatus() == MessageType.Success.value()) {
			logger.info("注册客户端开启定时心跳任务！");
			heartBeat = ctx.executor().scheduleAtFixedRate(
				    new HeartBeatTask(ctx), 0, 200000,
				    TimeUnit.MILLISECONDS);
		} else if (message.getCmdType() == MessageType.HEARTBEAT.value() && message.getOpStatus() == MessageType.Success.value()) {
			logger.info("注册客户端收到服务端返回的心跳回复消息！{}",message);
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
		logger.info("注册客户端发送心跳消息！{}",heartBeat);
	    ctx.writeAndFlush(heatBeat);
	}

	private Message buildHeatBeat() {
//		byte[] s = {1,2};
		Message message = new Message();
		Head head = new Head();
		head.setHead(MessageConstant.Header);
		head.setLength(0);
		message.setHead(head);;
		message.setCmdType(MessageType.HEARTBEAT.value());
//		message.setOptionData(s);
		message.setLength(18+head.getLength());
	    return message;
	}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	    throws Exception {
	cause.printStackTrace();
	if (heartBeat != null) {
		logger.error("注册客户端开始取消未执行的心跳任务！");
	    heartBeat.cancel(true);
	    heartBeat = null;
	}
	ctx.fireExceptionCaught(cause);
    }

}
