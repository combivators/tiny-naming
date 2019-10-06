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
    public <T> T lookup(ServicePoint point, Class<T> classType) {
        try {
            Registry registry = LocateRegistry.getRegistry(point.getHost(), point.getPort());
            return (T)registry.lookup(point.getObjectName());
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
