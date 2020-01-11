package net.tiny.context;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServiceLocator implements ServiceFeature {

    @Override
    public ServiceType getType() {
        return ServiceType.RMI;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T lookup(String endpoint, Class<T> classType) {
        ServicePoint point = ServicePoint.valueOf(endpoint);
        if (!ServiceType.RMI.equals(point.getServiceType())) {
            throw new IllegalArgumentException("'" + endpoint + "'");
        }
        try {
            Registry registry = LocateRegistry.getRegistry(point.getHost(), point.getPort());
            return (T)registry.lookup(point.getName());
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
