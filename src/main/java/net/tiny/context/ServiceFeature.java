package net.tiny.context;

public interface ServiceFeature {
    ServiceType getType();
    <T> T lookup(String endpoint, Class<T> classType);
}
