package com.joker.registration.container;

import com.joker.registration.client.strategy.ChooseStrategy;
import com.joker.registration.client.strategy.PollingChooseStrategy;


/**
 * 同一服务集群
 * Created by joker on 2017/12/7.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class ProviderSet {
    private final String serviceName;//服务名
    private final static int N_LOCKS = 16;//锁的个数
    private final int maxProvider;//最大的生产者数量
    private final Object[] locks;//分段锁
    private final ProviderPO[] providers;//生产者数组
    private ChooseStrategy strategy = new PollingChooseStrategy();//选择策略


    private final int hash(Object key) {
        return Math.abs(key.hashCode() % providers.length);
    }

    public ProviderSet(String serviceName) {
        this(10,serviceName);
    }


    public ProviderSet(int maxProvider,String serviceName) {
        this(serviceName,maxProvider,new PollingChooseStrategy());
    }

    public ProviderSet(String serviceName, int maxProvider, ChooseStrategy strategy) {
        this.serviceName = serviceName;
        this.maxProvider = maxProvider;
        this.strategy = strategy;
        locks = new Object[N_LOCKS];
        for (int i = 0 ; i < locks.length ; i ++) {
            locks[i] = 1;
        }
        providers = new ProviderPO[maxProvider];
    }

    //设置策略
    public void setStrategy(ChooseStrategy strategy) {
        this.strategy = strategy;
    }

    public void addProvider(Object key, ProviderPO provider) {
        int hash = hash(key);
        synchronized (locks[hash % maxProvider]) {
            providers[hash] = provider;
        }

    }

    public ProviderPO getProvider(Object key) {
        int hash = hash(key);
        synchronized (locks[hash % maxProvider]) {
            return providers[hash];
        }
    }

    public ProviderPO removeProvider(Object key) {
        int hash = hash(key);
        synchronized (locks[hash % maxProvider]) {
            ProviderPO provider = providers[hash];
            providers[hash] = null;
            return provider;
        }
    }

    public int getMaxProvider() {
        return maxProvider;
    }

    public ProviderPO getProviderByIndex(int index) {
        synchronized (locks[index % maxProvider]) {
            return providers[index];
        }
    }

    /**
     * 根据策略获取服务生产者
     * @return
     */
    public ProviderPO getProviderByStrategy() {
        return strategy.getProvider(this);
    }

    public String getServiceName() {
        return serviceName;
    }



}
