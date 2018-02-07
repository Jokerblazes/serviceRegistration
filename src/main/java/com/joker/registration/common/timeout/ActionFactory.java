package com.joker.registration.common.timeout;

import com.joker.agreement.entity.Message;
import com.joker.agreement.entity.MessageType;
import io.netty.channel.ChannelHandlerContext;
import rx.Observable;
import rx.functions.Action1;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;

/**
 * @Author Joker
 * @Description
 * @Date Create in 下午3:36 2018/2/5
 */
public class ActionFactory {

    /**
     * 创建Action并返回FutureTaskDecorator
     * @return
     */
    public static FutureTaskDecorator createActionAndReturnFuture(final ChannelHandlerContext ctx, final String uri, final int dest, final int source) {
        MessageCallable messageCallable = new MessageCallable();
        FutureTaskDecorator futureTaskDecorator = new FutureTaskDecorator(messageCallable);
        FutureTask futureTask = futureTaskDecorator.getFutureTask();
        Future future = futureTask;
        Action1<Future> action1 = new Action1<Future>() {
            @Override
            public void call(Future s) {
                Message message = null;
                try {
                    message = (Message) s.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    message = Message.messageResult(MessageType.Timeout.value(),uri);
                    message.setDest(dest);
                    message.setSource(source);
                } finally {
                    ctx.writeAndFlush(message);
                }
            }
        };
        Observable<Future> a = Observable.just(future);
        a.subscribe(action1);
        return futureTaskDecorator;
    }


}
