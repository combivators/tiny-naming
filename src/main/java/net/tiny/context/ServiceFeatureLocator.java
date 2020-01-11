package net.tiny.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tiny.service.ServiceContext;

public class ServiceFeatureLocator {

    private ServiceContext serviceContext;
    private List<String> features = new ArrayList<String>();
    private Map<ServiceType, ServiceFeature> map = null;

    public ServiceContext getServiceContext() {
        return serviceContext;
    }

    public void setServiceContext(ServiceContext sc) {
        serviceContext = sc;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> list) {
        this.features = list;
    }

    public <T> T lookup(String endpoint, Class<T> classType) {
        ServicePoint point = ServicePoint.valueOf(endpoint);
        ServiceFeature feature = feature(point.getServiceType());
        if (null != feature) {
            return feature.lookup(endpoint, classType);
        } else {
            return null;
        }
    }

    protected ServiceFeature feature(ServiceType type) {
        return getFeatureMap().get(type);
    }

    private Map<ServiceType, ServiceFeature> getFeatureMap() {
        if (map == null) {
            map = new HashMap<>();
            if (features == null || features.isEmpty()) {
                final LocalServiceLocator locator =  new LocalServiceLocator(serviceContext);
                map.put(ServiceType.LOCAL, locator);
            } else {
                for (String alias : features) {
                    ServiceFeature feature = serviceContext.lookup(alias, ServiceFeature.class);
                    map.put(feature.getType(), feature);
                }
            }
        }
        return map;
    }
}
