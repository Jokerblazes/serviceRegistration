package com.joker.registration.handler;

import com.joker.agreement.entity.Message;
import com.joker.agreement.entity.MessageType;
import com.joker.registration.Client;
import com.joker.registration.CustomerClient;
import com.joker.registration.container.ProviderContainer;
import com.joker.registration.container.ProviderPO;
import com.joker.registration.dto.CustomerDTO;
import com.joker.registration.dto.Node;
import com.joker.registration.dto.Provider;
import com.joker.registration.dto.ProviderList;
import com.joker.registration.utils.ClientType;
import com.joker.registration.utils.MessagePackageFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ArrayBlockingQueue;


/**
 * Created by joker on 2017/12/8.
 */
public class RegistHandler extends SimpleChannelInboundHandler<Object> {
    private final int flag;
    private final Object dto;
    private final EventLoopGroup group;
    private final EventLoopGroup bossGroup;

    public RegistHandler(int flag,Object dto,EventLoopGroup group,EventLoopGroup bossGroup) {
        this.flag = flag;
        this.dto = dto;
        this.group = group;
        this.bossGroup = bossGroup;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildRegist());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //URI serviceList providerList
        //URI singleService provider
        if (flag == ClientType.CUSTOMER.value()) {
            Message message = (Message) msg;
            String uri = new String(message.getHead().getUrl());
            ProviderContainer container = ProviderContainer.getInstance();
            if ("serviceList".equals(uri)) {
                ProviderList list = (ProviderList) MessagePackageFactory.bytesToEntity(message.getOptionData(), ProviderList.class);
                Provider[] providers = list.getProviders();
                int length = providers.length;
                for (int i = 0 ; i < length ; i++)
                    addProvider(ctx,providers[i],container,group);
            } else if ("singleAdd".equals(uri)) {
                Provider provider = (Provider) MessagePackageFactory.bytesToEntity(message.getOptionData(), Provider.class);
                addProvider(ctx,provider,container,group);
            } else {
                Provider provider = (Provider) MessagePackageFactory.bytesToEntity(message.getOptionData(), Provider.class);
                container.removeProvider(provider);
            }
        }
    }



    private Message buildRegist() {
        Message message = null;
        if (flag == ClientType.CUSTOMER.value()) {
            byte[] bytes = MessagePackageFactory.entityToBytes(dto);
            message = Message.messageResult(bytes, MessageType.Success.value(),"");
            message.setCmdType(MessageType.CUSTOMER_REGIST.value());
        } else if (flag == ClientType.PROVIDE.value()) {
            byte[] bytes = MessagePackageFactory.entityToBytes(dto);
            message = Message.messageResult(bytes, MessageType.Success.value(),"");
            message.setCmdType(MessageType.PROVIDER_REGIST.value());
        } else {
            throw new RuntimeException("非法的flag");
        }
        return message;
    }

    private void addProvider(ChannelHandlerContext ctx,Provider provider,ProviderContainer container,EventLoopGroup group) {
        if (provider == null)
            return;
//        final int capacity = 20;
//        final ProviderPO providerPO = new ProviderPO(provider,new ArrayBlockingQueue<Message>(20));
        final ProviderPO providerPO = new ProviderPO(provider);
        Runnable runnable = createRunnable(providerPO,group);
        bossGroup.execute(runnable);
        container.addProvider(providerPO);
    }

    Runnable createRunnable(final ProviderPO providerPO,EventLoopGroup group) {
        final CustomerClient client = new CustomerClient(group);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    client.connect(providerPO.getNode().getIp(),providerPO.getNode().getPort(),providerPO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return runnable;
    }

}
