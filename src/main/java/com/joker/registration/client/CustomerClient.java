package com.joker.registration.client;

import com.joker.agreement.codec.MessageDecoder;
import com.joker.agreement.codec.MessageEncoder;
import com.joker.agreement.entity.MessageConstant;
import com.joker.registration.container.ProviderPO;
import com.joker.registration.client.handler.CustomerHeartBeatReqHandler;
import com.joker.registration.client.handler.LoginHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 消费者-服务者客户端
 * Created by joker on 2017/12/8.
 */
public class CustomerClient {
    private final static int reConnectTimes = 3;
    private CountDownLatch count = new CountDownLatch(reConnectTimes);
    private int timeOut;//重连间隔
    private EventLoopGroup group;
    private Channel channel;//通道
    private ProviderPO provider;//生产者

    private final static Logger logger = LoggerFactory.getLogger(CustomerClient.class);

    public CustomerClient(EventLoopGroup eventLoopGroup,ProviderPO provider) {
        this(5,eventLoopGroup,provider);
    }

    public CustomerClient(int timeOut, EventLoopGroup group,ProviderPO provider) {
        this.timeOut = timeOut;
        this.group = group;
        this.provider = provider;
    }


    /**
     * 连接
     * @throws Exception
     */
    public void connect() throws Exception {
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
                            ch.pipeline().addLast(new LoginHandler(provider));
                            ch.pipeline().addLast(new CustomerHeartBeatReqHandler(provider));
                        }
                    });
            // 发起异步连接操作
            ChannelFuture future = b.connect(provider.getNode().getIp(),provider.getNode().getPort()).sync();
            channel = future.channel();
            logger.info("连接成功！{}",channel);
            future.channel().closeFuture().sync();
        } finally {
            group.execute(new Runnable() {
                public void run() {
                    reConnect();
                }
            });
        }
    }

    /**
     * 断线重连
     */
    public void reConnect() {
        if (count.getCount() == 0) {
            logger.info("到达重连次数上限，重连失败！");
            return;
        }
        count.countDown();
        try {
            TimeUnit.SECONDS.sleep(1);
            try {
                logger.info("发起第{}次重连 {}",count.getCount(), provider);
                connect();// 发起重连操作
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
