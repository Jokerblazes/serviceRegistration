package com.joker.registration.dto;

import org.msgpack.annotation.Message;


@Message
public class ProviderList {
    private Provider[] providers;
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