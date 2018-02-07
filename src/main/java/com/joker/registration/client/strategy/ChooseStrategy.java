package com.joker.registration.client.strategy;

import com.joker.registration.container.ProviderPO;
import com.joker.registration.container.ProviderSet;

/**
 * 负载均衡策略
 * Created by joker on 2017/12/8.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public interface ChooseStrategy {
    ProviderPO getProvider(ProviderSet providerSet);
}
