package com.joker.registration.dto;

import entity.NonEmpty;
import org.msgpack.annotation.Message;

import java.util.Arrays;


/**
 * 生产者list
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
@Message
public class ProviderList {
    @NonEmpty
    private ProviderDTO[] providers;
    @NonEmpty
    private String serviceName;


    public ProviderList() {
    }

    public ProviderList(ProviderDTO[] providers, String serviceName) {
        this.providers = providers;
        this.serviceName = serviceName;
    }

    public ProviderDTO[] getProviders() {
        return providers;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setProviders(ProviderDTO[] providers) {
        this.providers = providers;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "ProviderList{" +
                "providers=" + Arrays.toString(providers) +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }
}