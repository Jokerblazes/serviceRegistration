package com.joker.registration.handler;

import com.joker.agreement.entity.Message;
import com.joker.agreement.entity.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务提供者处理类
 * Created by joker on 2017/12/10.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class ServiceHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(ServiceHandler.class);
    private final ServiceDeal deal;

    public ServiceHandler(ServiceDeal deal) {
        this.deal = deal;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("ServiceHandler新的链接进入 {}",ctx.channel().toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("ServiceHandler链接异常，关闭 {}",ctx.channel().toString());
        exceptionalProcess(ctx);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("ServiceHandler链接断开 {}",ctx.channel().toString());
        exceptionalProcess(ctx);
    }

    //心跳检测
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.state() == IdleState.ALL_IDLE){
                logger.info("长时间不读写，断开连接 {}",ctx.channel().toString());
                ctx.channel().close();
            }
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("服务器收到消息 {}",msg);
        Message reqMessage = (Message)msg;
        Message message = null;
        //如果软件登录
        if(reqMessage.getCmdType() == MessageType.Login.value()) {
            message = reqMessage;
            message.setOpStatus(MessageType.Success.value());
        } else {
            logger.info("业务处理！");
            message =  deal.handleAndReturnMessage((Message)msg,ctx);
        }
        logger.info("处理结束 {}",message);
        if(message!=null) {
            ctx.writeAndFlush(message);
        }else {
            logger.info("没有对应命令执行！或内部出错！");
            ctx.channel().close();
        }
    }
    //异常处理
    public  void  exceptionalProcess(ChannelHandlerContext ctx) {
    }

}

