package com.joker.registration.server.handler;

import com.joker.agreement.entity.Message;
import com.joker.agreement.entity.MessageType;
import com.joker.registration.client.CustomerClient;
import com.joker.registration.container.ProviderContainer;
import com.joker.registration.container.ProviderPO;
import com.joker.registration.dto.ProviderDTO;
import com.joker.registration.dto.ProviderList;
import com.joker.registration.utils.ClientType;
import com.joker.registration.utils.MessagePackageFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CheckUntils;


/**
 * 客户端注册类
 * Created by joker on 2017/12/8.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class RegistHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RegistHandler.class);

    private final int flag;//消费者 or 生产者
    private final Object dto; //消费者 or 生产者实体
    private final EventLoopGroup group;//工作线程组
    private final EventLoopGroup bossGroup;//主线程组

    public RegistHandler(int flag,Object dto,EventLoopGroup group,EventLoopGroup bossGroup) {
        this.flag = flag;
        this.dto = dto;
        this.group = group;
        this.bossGroup = bossGroup;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("建立新的链路-注册链路{}",ctx);
        ctx.writeAndFlush(buildRegist());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //URI serviceList providerList
        //URI singleService provider
        if (flag == ClientType.CUSTOMER.value()) {
            Message message = (Message) msg;
            byte[] bytes = message.getHead().getUrl();
            String uri = null;
            if (bytes != null)
                uri = new String(bytes);
            ProviderContainer container = ProviderContainer.getInstance();
            if ("serviceList".equals(uri)) {
                ProviderList list = (ProviderList) MessagePackageFactory.bytesToEntity(message.getOptionData(), ProviderList.class);
                logger.info("首次获取生产者list{}",list);
                //检查对象是否为空
                if (!CheckUntils.checkNull(list)) {
                    throw new RuntimeException("没有对应的生成者！");
                }
                ProviderDTO[] providers = list.getProviders();
                int length = providers.length;
                for (int i = 0 ; i < length ; i++)
                    addProvider(ctx,providers[i],container,group);
            } else if ("singleAdd".equals(uri)) {
                ProviderDTO provider = (ProviderDTO) MessagePackageFactory.bytesToEntity(message.getOptionData(), ProviderDTO.class);
                //检查对象是否为空
                if (!CheckUntils.checkNull(provider)) {
                    logger.error("注册中心返回生产者为空！新增操作失败！");
//                    throw new RuntimeException("注册中心返回生产者为空！新增操作失败！");
                }
                addProvider(ctx,provider,container,group);
            } else {
                ProviderDTO provider = (ProviderDTO) MessagePackageFactory.bytesToEntity(message.getOptionData(), ProviderDTO.class);
                if (!CheckUntils.checkNull(provider)) {
                    logger.error("注册中心返回生产者为空！删除操作失败！");
//                    throw new RuntimeException("注册中心返回生产者为空！删除操作失败！");
                }
                container.removeProvider(provider);
            }
        }
    }



    private Message buildRegist() {
        Message message = null;
        if (!CheckUntils.checkNull(dto)) {
//            logger.error("对象有非空对象为空！");
            throw new RuntimeException("对象有非空对象为空！");
        }
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

    /**
        super.exceptionCaught(ctx, cause);
        logger.error(cause.getMessage() + "{}",ctx);
     * 添加生产者
     * @param ctx
     * @param provider
     * @param container
     * @param group
     */
    private void addProvider(ChannelHandlerContext ctx,ProviderDTO provider,ProviderContainer container,EventLoopGroup group) {
        if (provider == null)
            return;
        final ProviderPO providerPO = new ProviderPO(provider);
        logger.info("添加生产者:{}",providerPO);
        Runnable runnable = createRunnable(providerPO,group);
        bossGroup.execute(runnable);

        container.addProvider(providerPO);
    }


    Runnable createRunnable(final ProviderPO providerPO,EventLoopGroup group) {
        final CustomerClient client = new CustomerClient(group,providerPO);
        logger.info("消费者客户端 {}",client);
        Runnable runnable = new Runnable() {
            public void run() {
                int capacity = providerPO.getCapacity();
                for (int i = 0; i < capacity; i++) {
                    try {
                        client.connect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        return runnable;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error(cause.getMessage() + "{}",ctx);
    }

}
