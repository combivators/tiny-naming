package net.tiny.context;

public interface ServiceFeature {
    ServiceType getType();
    <T> T lookup(ServicePoint point, Class<T> classType);
}
