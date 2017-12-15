package com.joker.registration.container;

import com.joker.registration.dto.Provider;
import com.joker.registration.strategy.ChooseStrategy;
import com.joker.registration.strategy.PollingChooseStrategy;
import com.joker.registration.strategy.RandomStrategy;

import java.util.Random;


/**
 * Created by joker on 2017/12/7.
 */
public class ProviderSet {
    private final String serviceName;
    private final static int N_LOCKS = 16;
    private final int maxProvider;
    private final Object[] locks;
    private final ProviderPO[] providers;
    private ChooseStrategy strategy = new PollingChooseStrategy();


    private final int hash(Object key) {
        return Math.abs(key.hashCode() % providers.length);
    }

    public ProviderSet(int maxProvider,String serviceName) {
        this.serviceName = serviceName;
        this.maxProvider = maxProvider;
        locks = new Object[N_LOCKS];
        for (int i = 0 ; i < locks.length ; i ++) {
            locks[i] = 1;
        }
        providers = new ProviderPO[maxProvider];
    }

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

    public ProviderPO getProviderByStrategy() {
        return strategy.getProvider(this);
    }

    public String getServiceName() {
        return serviceName;
    }



}
