//package com.joker.registration.container;
//
//import com.joker.agreement.entity.Message;
//
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//
///**
// * Created by joker on 2017/12/12.
// * https://github.com/Jokerblazes/serviceRegistration.git
// */
//public class QueueContainer {
//    private static QueueContainer queueContainer = new QueueContainer();
//
//    public static QueueContainer getInstance() {
//        return queueContainer;
//    }
//
//    private final BlockingQueue<Message> messageQueue;
//
//
//
//    public QueueContainer() {
//        this.messageQueue = new ArrayBlockingQueue<Message>(20);
//    }
//
//    public BlockingQueue<Message> getMessageQueue() {
//        return messageQueue;
//    }
//}
