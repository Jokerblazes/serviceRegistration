package com.joker.registration;

import com.joker.agreement.codec.MessageDecoder;
import com.joker.agreement.codec.MessageEncoder;
import com.joker.agreement.entity.MessageConstant;
import com.joker.registration.container.ProviderContainer;
import com.joker.registration.dto.Provider;
import com.joker.registration.handler.HeartBeatReqHandler;
import com.joker.registration.handler.RegistHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joker on 2017/12/8.
 */
public class Client {

//    public Client(int flag,Provider provider) {
//        this.registHandler = new RegistHandler(flag,provider);
//    }
    private Channel channel;
    public Client(int flag,Object dto,EventLoopGroup group,EventLoopGroup bossGroup) {
        this.registHandler = new RegistHandler(flag,dto,group,bossGroup);
        this.group = group;
//        this.bossGroup = bossGroup;
    }

    private EventLoopGroup group;

//    private EventLoopGroup bossGroup;
    private final RegistHandler registHandler;


    public Client initProviderMap(List<String> serviceName) {
        ProviderContainer container = ProviderContainer.getInstance();
        container.initMap(serviceName);
        return this;
    }

    public void connect(String host,int port) throws Exception {

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
                            ch.pipeline().addLast(new HeartBeatReqHandler());
                            ch.pipeline().addLast(registHandler);
                        }
                    });
            // 发起异步连接操作
            ChannelFuture future = b.connect(host,port).sync();
            this.channel = future.channel();
            future.channel().closeFuture().sync();
        } finally {

        }
    }

    public void close() {
        ChannelFuture future = channel.close();
    }


//    public static void main(String[] args) {
//        Client client = new Client();
//        ProviderPO provider4 = new ProviderPO();
//        provider4.setServiceName("order");
//        Node node4 = new Node();
//        node4.setIp("127.0.0.1");
//        node4.setId(1);
//        node4.setPort(8080);
//        provider4.setNode(node4);
//        RegistHandler registHandler = new RegistHandler(ClientType.PROVIDE.value(),provider4);
//        client.setRegistHandler(registHandler);
//        try {
//            client.connect("127.0.0.1",8888);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}
