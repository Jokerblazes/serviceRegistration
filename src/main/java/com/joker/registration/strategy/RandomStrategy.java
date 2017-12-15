package com.joker.registration.strategy;

import com.joker.registration.container.ProviderPO;
import com.joker.registration.container.ProviderSet;
import com.joker.registration.dto.Provider;

import java.util.Random;

/**
 * 随机策略
 * Created by joker on 2017/12/8.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class RandomStrategy implements ChooseStrategy {
    private final Random random;
    private final int max;

    public RandomStrategy(int max) {
        this.max = max;
        random = new Random(max);
    }

    public ProviderPO getProvider(ProviderSet providerSet) {
        return ramdomProvider(providerSet);
    }

    private ProviderPO ramdomProvider(ProviderSet providerSet) {
        int count = 0;
        ProviderPO provider = null;
        while (provider == null) {
            int index = random.nextInt();
            provider = providerSet.getProviderByIndex(index);
            if (count/2 > max)
                break;
            count ++;
        }
        return provider;
    }
}
