package com.joker.registration.common.timeout;

import java.util.concurrent.FutureTask;

/**
 * @Author Joker
 * @Description
 * @Date Create in 下午2:37 2017/12/29
 */
public class FutureTaskDecorator {

    private FutureTask futureTask;
    private MessageCallable callable;
    public FutureTaskDecorator(MessageCallable callable) {
        this.callable = callable;
        this.futureTask = new FutureTask(callable);
    }

    public FutureTask getFutureTask() {
        return futureTask;
    }


    public void run() {
        this.futureTask.run();
    }

    public MessageCallable getCallable() {
        return callable;
    }

    public void setCallable(MessageCallable callable) {
        this.callable = callable;
    }
}
