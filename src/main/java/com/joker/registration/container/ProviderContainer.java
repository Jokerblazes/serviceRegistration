package com.joker.registration.container;


import com.joker.registration.dto.Provider;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务提供者容器
 * Created by joker on 2017/12/8.
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
public class ProviderContainer {
    private final static ProviderContainer container = new ProviderContainer();

    public static ProviderContainer getInstance() {
        return container;
    }


    private final ConcurrentMap<String,ProviderSet> map = new ConcurrentHashMap<String, ProviderSet>();
    private final Boolean isUse = false;

    /**
     * 初始化容器
     * @param serviceNames
     */
    public void initMap(List<String> serviceNames) {
        if (serviceNames == null)
            throw new RuntimeException("初始化入参不允许为空！");
        if (serviceNames.size() == 0)
            throw new RuntimeException("初始化入参大小不能为0");
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

    /**
     * 添加服务生产者
     * @param provider
     */
    public void addProvider(ProviderPO provider) {
        ProviderSet providerSet = map.get(provider.getServiceName());
        if (providerSet == null)
            throw new RuntimeException("没有对应的服务！");
        providerSet.addProvider(provider.getNode().getId(),provider);
    }

    /**
     * 移除服务生产者
     * @param provider
     */
    public void removeProvider(Provider provider) {
        ProviderSet providerSet = map.get(provider.getServiceName());
        if (providerSet == null)
            throw new RuntimeException("没有对应的服务可被移除！");
        providerSet.removeProvider(provider.getNode().getId());
    }

    /**
     * 获取服务生产者
     * @param serviceName
     * @return
     */
    public ProviderPO getProvider(String serviceName) {
        ProviderSet set = map.get(serviceName);
        if (set == null)
            throw new RuntimeException("没有对应的服务！");
        return set.getProviderByStrategy();
    }


}
