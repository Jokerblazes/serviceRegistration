package com.joker.registration.handler;


import com.joker.agreement.entity.Head;
import com.joker.agreement.entity.Message;
import com.joker.agreement.entity.MessageConstant;
import com.joker.agreement.entity.MessageType;
import com.joker.registration.container.ChannelContainer;
import com.joker.registration.container.ProviderPO;
import com.joker.registration.container.StorageContainer;
import com.joker.registration.entity.MessageChannel;
import com.joker.registration.entity.Storage;
import com.joker.registration.runnable.MessageChannelCreater;
import com.joker.registration.utils.Constent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
		provider.setCtx(ctx);
		ctx.writeAndFlush(buildLogin());
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		Message message = (Message)msg;
		logger.info("消费者端收到的消息 {}",message);
		if (MessageType.Other.value() == message.getCmdType()) {
			logger.info("业务消息！");
			ChannelContainer actionContainer = ChannelContainer.getContainer();
			String uri = new String(message.getHead().getUrl());
			//TODO 收到消息并组装成MessageAction，作为生产者将数据最后放入有界限缓存
			int id = message.getDest();
			ChannelHandlerContext ctx1 = (ChannelHandlerContext)ChannelContainer.getContainer().get(uri + Constent.ID + id);
			if (ctx1 == null) {
				logger.error("没有直连的链路！");
				throw new RuntimeException("没有直连的链路！");
			}
//			Action1<Message> action1 = (Action1<Message>)ActionContainer.getContainer().get(uri + Constent.ID + id);
			MessageChannel messageChannel = new MessageChannel(ctx1,message);
			StorageContainer storageContainer = StorageContainer.getInstance();
			Storage<MessageChannel> storage = storageContainer.get(message.getSource());
			if (storage == null) {
				logger.error("缓存命中失败！");
				throw new RuntimeException("缓存命中失败！");
			}
			MessageChannelCreater creater = new MessageChannelCreater(storage,messageChannel);
			ctx.executor().execute(creater);
//			ctx.channel().eventLoop().parent().execute(creater);
//			Observable observable = Observable.just(message);
//			observable.subscribe(action1);
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
