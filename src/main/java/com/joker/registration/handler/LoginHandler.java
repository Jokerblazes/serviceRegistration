package com.joker.registration.handler;


import com.joker.agreement.entity.Head;
import com.joker.agreement.entity.Message;
import com.joker.agreement.entity.MessageConstant;
import com.joker.agreement.entity.MessageType;
import com.joker.registration.container.ActionContainer;
import com.joker.registration.container.ProviderPO;
import com.joker.registration.container.StorageContainer;
import com.joker.registration.entity.MessageAction;
import com.joker.registration.entity.Storage;
import com.joker.registration.runnable.MessageActionCreater;
import com.joker.registration.utils.Constent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import rx.functions.Action1;



public class LoginHandler extends SimpleChannelInboundHandler<Object> {
	private ProviderPO provider;

	public LoginHandler(ProviderPO provider) {
		this.provider = provider;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		provider.setCtx(ctx);
		ctx.writeAndFlush(buildLogin());
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		Message message = (Message)msg;
		System.out.println("这是消费者端收到的消息："+message);
		if (MessageType.Other.value() == message.getCmdType()) {
			ActionContainer actionContainer = ActionContainer.getContainer();
			String uri = new String(message.getHead().getUrl());
			//TODO 收到消息并组装成MessageAction，作为生产者将数据最后放入有界限缓存
			int id = message.getDest();
			ChannelHandlerContext ctx1 = (ChannelHandlerContext)ActionContainer.getContainer().get(uri + Constent.ID + id);
//			Action1<Message> action1 = (Action1<Message>)ActionContainer.getContainer().get(uri + Constent.ID + id);
			MessageAction messageAction = new MessageAction(ctx1,message);
			StorageContainer storageContainer = StorageContainer.getInstance();
			Storage<MessageAction> storage = storageContainer.get(message.getSource());
			MessageActionCreater creater = new MessageActionCreater(storage,messageAction);
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
