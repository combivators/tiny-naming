package net.tiny.context;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

public class IIOPServiceLocator implements ServiceFeature {

    @Override
    public ServiceType getType() {
        return ServiceType.IIOP;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T lookup(ServicePoint point, Class<T> classType) {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.cosnaming.CNCtxFactory");
        env.put(Context.PROVIDER_URL, String.format("iiop://%s:%d", point.getHost() , point.getPort()));
        try {
            final Context context =  new InitialContext(env);
            Object objref = context.lookup(point.getObjectName());
            return (T)PortableRemoteObject.narrow(objref, classType);
        } catch (NamingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
