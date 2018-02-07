package com.joker.registration.client.handler;


import com.joker.agreement.entity.Head;
import com.joker.agreement.entity.Message;
import com.joker.agreement.entity.MessageConstant;
import com.joker.agreement.entity.MessageType;
import com.joker.registration.common.timeout.FutureTaskDecorator;
import com.joker.registration.common.timeout.MessageCallable;
import com.joker.registration.container.FutureTaskContainer;
import com.joker.registration.container.ProviderPO;
import com.joker.registration.utils.UriUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.FutureTask;


/**
 * 消费者-服务提供者处理类
 * Created by joker on 2017/12/12.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class LoginHandler extends SimpleChannelInboundHandler<Object> {
	private final static Logger logger = LoggerFactory.getLogger(LoginHandler.class);

	private ProviderPO provider;

	public LoginHandler(ProviderPO provider) {
		this.provider = provider;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("建立新的链路->消费者-生成者链路{}",ctx);
		//并将链路加入到对应的生产者对象中
		provider.addCtx(ctx);
//		provider.setCtx(ctx);
		ctx.writeAndFlush(buildLogin());
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		Message message = (Message)msg;
		logger.info("消费者端收到的消息 {}",message);
		if (MessageType.Other.value() == message.getCmdType()) {
			//1:从容器中取future
			FutureTaskContainer container = provider.getContainer();
			String key = UriUtils.getStringFromMessage(message);
			logger.info("对应处理的键 {}",key);
			FutureTaskDecorator futureTaskDecorator = container.get(key);
			if (futureTaskDecorator == null) {
				logger.info("键{}没有对应的值!", key);
				throw new RuntimeException("缓存命中失败！");
			}
			//2:将消息放入
			MessageCallable callable = futureTaskDecorator.getCallable();
			FutureTask futureTask = futureTaskDecorator.getFutureTask();
			callable.setMessage(message);
        	futureTask.run();


		} else {
			ctx.fireChannelRead(msg);
		}
	}

	private Message buildLogin() {
		Message message = new Message();
		Head head = new Head();
		head.setHead(MessageConstant.Header);
		head.setLength(0);
		head.setUrl(null);
		message.setHead(head);
		message.setCmdType(MessageType.Login.value());
		message.setLength(18+head.getLength());
		return message;
	}

}
