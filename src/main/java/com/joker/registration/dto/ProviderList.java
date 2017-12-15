package com.joker.registration.dto;

import entity.NonEmpty;
import org.msgpack.annotation.Message;


/**
 * 生产者list
 * https://github.com/Jokerblazes/serviceRegistration.git
 */
@Message
public class ProviderList {
    @NonEmpty
    private Provider[] providers;
    @NonEmpty
    private String serviceName;


    public ProviderList() {
    }

    public ProviderList(Provider[] providers, String serviceName) {
        this.providers = providers;
        this.serviceName = serviceName;
    }

    public Provider[] getProviders() {
        return providers;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setProviders(Provider[] providers) {
        this.providers = providers;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}