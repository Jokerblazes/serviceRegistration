package com.joker.registration;

import com.joker.agreement.codec.MessageDecoder;
import com.joker.agreement.codec.MessageEncoder;
import com.joker.agreement.entity.MessageConstant;
import com.joker.registration.container.ProviderContainer;
import com.joker.registration.container.ProviderPO;
import com.joker.registration.dto.Provider;
import com.joker.registration.handler.CustomerHeartBeatReqHandler;
import com.joker.registration.handler.HeartBeatReqHandler;
import com.joker.registration.handler.LoginHandler;
import com.joker.registration.handler.RegistHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.awt.*;
import java.util.List;

/**
 * 消费者-服务者客户端
 * Created by joker on 2017/12/8.
 */
public class CustomerClient {
    private EventLoopGroup group;
    public CustomerClient(EventLoopGroup eventLoopGroup) {
        this.group = eventLoopGroup;
    }



    public void connect(String host, int port,final ProviderPO providerPO) throws Exception {

        // 配置客户端NIO线程组

        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    new MessageDecoder(
                                            Integer.MAX_VALUE, MessageConstant.lengthFieldOffset,
                                            MessageConstant.lengthFieldLength,MessageConstant.lengthAdjustment,MessageConstant.initialBytesToStrip));
                            ch.pipeline().addLast(
                                    new MessageEncoder());
                            ch.pipeline().addLast(new LoginHandler(providerPO));
                            ch.pipeline().addLast(new CustomerHeartBeatReqHandler(providerPO));
                        }
                    });
            // 发起异步连接操作
            ChannelFuture future = b.connect(host,port).sync();
            future.channel().closeFuture().sync();
        } finally {

        }
    }


}
