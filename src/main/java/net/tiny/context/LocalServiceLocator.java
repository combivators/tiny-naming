package net.tiny.context;

import net.tiny.service.ServiceContext;

public class LocalServiceLocator implements ServiceFeature {

    private ServiceContext serviceContext;

    public LocalServiceLocator() {}

    public LocalServiceLocator(ServiceContext sc) {
        serviceContext = sc;
    }

    @Override
    public ServiceType getType() {
        return ServiceType.LOCAL;
    }

    @Override
    public <T> T lookup(String endpoint, Class<T> classType) {
        return serviceContext.lookup(endpoint, classType);
    }
}
