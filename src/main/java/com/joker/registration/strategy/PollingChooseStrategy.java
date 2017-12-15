package com.joker.registration.strategy;

import com.joker.registration.container.ProviderPO;
import com.joker.registration.container.ProviderSet;
import com.joker.registration.dto.Provider;

/**
 * 轮询策略
 * Created by joker on 2017/12/8.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class PollingChooseStrategy implements ChooseStrategy {
    private int index = 0;


    public ProviderPO getProvider(ProviderSet providerSet) {
        int start = index;
        ProviderPO provider =  poolingSet(providerSet);
        if (provider == null && start != 0) {
            index = 0;
            start = index;
            provider = poolingSet(providerSet);
        }
        return provider;
    }

    private ProviderPO poolingSet(ProviderSet providerSet) {
        final int max = providerSet.getMaxProvider();
        ProviderPO provider = null;
        while (provider == null && index+1 != max) {
            provider = providerSet.getProviderByIndex(index);
            index++;
        }
        return provider;
    }
}
