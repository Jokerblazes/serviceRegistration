package com.joker.registration.handler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


import com.joker.agreement.entity.Head;
import com.joker.agreement.entity.Message;
import com.joker.agreement.entity.MessageConstant;
import com.joker.agreement.entity.MessageType;
import com.joker.registration.container.ProviderContainer;
import com.joker.registration.container.QueueContainer;
import com.joker.registration.runnable.MessageDestroyer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HeartBeatReqHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		Message message = (Message) msg;
		// 握手成功，主动发送心跳消息
		if (message.getCmdType() == MessageType.Login.value() && message.getOpStatus() == MessageType.Success.value()) {
			heartBeat = ctx.executor().scheduleAtFixedRate(
				    new HeartBeatTask(ctx), 0, 200000,
				    TimeUnit.MILLISECONDS);
		} else if (message.getCmdType() == MessageType.HEARTBEAT.value() && message.getOpStatus() == MessageType.Success.value()) {
			System.out
		    .println("Client receive server heart beat message : ---> "
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
		    .println("Client send heart beat messsage to server : ---> "
			    + heatBeat);
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
	    heartBeat.cancel(true);
	    heartBeat = null;
	}
	ctx.fireExceptionCaught(cause);
    }

}
