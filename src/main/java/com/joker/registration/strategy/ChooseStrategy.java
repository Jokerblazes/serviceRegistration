package com.joker.registration.strategy;

import com.joker.registration.container.ProviderPO;
import com.joker.registration.container.ProviderSet;
import com.joker.registration.dto.Provider;

/**
 * Created by joker on 2017/12/8.
 */
public interface ChooseStrategy {
    ProviderPO getProvider(ProviderSet providerSet);
}
