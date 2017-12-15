package com.joker.registration.container;


import com.joker.registration.dto.Provider;
import com.joker.registration.dto.ProviderList;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by joker on 2017/12/8.
 */
public class ProviderContainer {
    private final static ProviderContainer container = new ProviderContainer();

    public static ProviderContainer getInstance() {
        return container;
    }


    private final ConcurrentMap<String,ProviderSet> map = new ConcurrentHashMap<String, ProviderSet>();
    private final Boolean isUse = false;

    //只允许初始化时使用
    public void initMap(List<String> serviceNames) {
        synchronized (isUse) {
            if (!isUse)
                for (String serviceName:
                        serviceNames) {
                    ProviderSet set = new ProviderSet(10,serviceName);
                    map.put(serviceName,set);
                }
        }
    }

//    public void addProvider(ProviderList providerList) {
//        ProviderSet providerSet = map.get(providerList.getServiceName());
//        Provider[] providers = providerList.getProviders();
//        for (ProviderPO provider:
//             providers) {
//            providerSet.addProvider(provider.getNode().getId(),provider);
//        }
//    }

    public void addProvider(ProviderPO provider) {
        ProviderSet providerSet = map.get(provider.getServiceName());
        providerSet.addProvider(provider.getNode().getId(),provider);
    }

    public void removeProvider(Provider provider) {
        ProviderSet providerSet = map.get(provider.getServiceName());
        providerSet.removeProvider(provider.getNode().getId());
    }

    public ProviderPO getProvider(String serviceName) {
        ProviderSet set = map.get(serviceName);
        return set.getProviderByStrategy();
    }


}
