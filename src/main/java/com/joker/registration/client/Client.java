package com.joker.registration.client;

import com.joker.agreement.codec.MessageDecoder;
import com.joker.agreement.codec.MessageEncoder;
import com.joker.agreement.entity.MessageConstant;
import com.joker.registration.container.ProviderContainer;
import com.joker.registration.client.handler.HeartBeatReqHandler;
import com.joker.registration.server.handler.RegistHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 注册客户端
 * Created by joker on 2017/12/8.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class Client {
    private final static int reConnectTimes = 3;
    private CountDownLatch count = new CountDownLatch(reConnectTimes);
    private EventLoopGroup group;
    private Channel channel;
    private final RegistHandler registHandler;
    private int timeOut;//重连间隔


    private final static Logger logger = LoggerFactory.getLogger(Client.class);


    /**
     *
     * @param clientType
     * @param dto 客户端基本信息
     * @param group
     * @param bossGroup
     */
    public Client(int clientType,Object dto,EventLoopGroup group,EventLoopGroup bossGroup) {
        this(clientType,dto,group,bossGroup,5);
    }

    /**
     *
     * @param clientType
     * @param dto 客户端基本信息
     * @param group
     * @param bossGroup
     * @param timeOut
     */
    public Client(int clientType,Object dto,EventLoopGroup group,EventLoopGroup bossGroup,int timeOut) {
        this.registHandler = new RegistHandler(clientType,dto,group,bossGroup);
        this.group = group;
        this.timeOut = timeOut;
        logger.info("客户端信息:重连间隔 {},重连次数 {}",timeOut,reConnectTimes);
    }


    //TODO 未知作用
    public Client initProviderMap(List<String> serviceName) {
        ProviderContainer container = ProviderContainer.getInstance();
        container.initMap(serviceName);
//        init = true;
        return this;
    }

    /**
     * 客户端连接
     * @param host
     * @param port
     * @throws Exception
     */
    public void connect(final String host, final int port) throws Exception {
//        if (!init)
//            throw new RuntimeException("请先调用initProviderMap方法！");
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
            logger.info("连接成功！");
            future.channel().closeFuture().sync();
        } finally {
            group.execute(new Runnable() {
                public void run() {
                    reConnect(host,port);
                }
            });

        }
    }

    /**
     * 主动关闭连接
     */
    public void close() {
        logger.info("主动关闭连接！{}",channel);
        ChannelFuture future = channel.close();
    }

    /**
     * 断线重连
     * @param host
     * @param port
     */
    public void reConnect(final String host,final int port) {
        if (count.getCount() == 0) {
            logger.info("到达重连次数上限，重连失败！");
            return;
        }
        count.countDown();
        try {
            TimeUnit.SECONDS.sleep(1);
            try {
                logger.info("发起第{}次重连 {}:{}",count.getCount(),host,port);
                connect(host,port);// 发起重连操作
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
