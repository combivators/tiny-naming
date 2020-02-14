package net.tiny.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.tiny.config.Reflections;
import net.tiny.ws.rs.client.RestClient;
import net.tiny.ws.rs.client.RestMethod;

public class RestWebServiceLocator implements ServiceFeature {

    static final Logger LOGGER = Logger.getLogger(RestWebServiceLocator.class.getName());

    @Override
    public ServiceType getType() {
        return ServiceType.REST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T lookup(String endpoint, Class<T> classType) {
        return (T)Proxy.newProxyInstance(classType.getClassLoader(),
                new Class[]{classType}, new ProxyHandler(classType, ServicePoint.valueOf(endpoint)));
    }

    class ProxyHandler implements InvocationHandler {

        private final Class<?> source;
        private final ServicePoint point;
        private final RestClient client;

        private <T> ProxyHandler(Class<T> type, ServicePoint sp) {
            this.source = type;
            this.point = sp;
            RestClient.Builder builder = new RestClient.Builder();
            if (point.hasCredentials()) {
                String[] credentials = point.getCredentials();
                builder = builder.credentials(credentials[0], credentials[1]);
            }
            this.client = builder.build();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            final RestMethod restMethod = new RestMethod(method, point.getPath());
            final URL url = new URL(restMethod.generateURI(args, point.getHost(), point.getPort(), point.isSecret()));
            Object ret = null;
            RestClient.Response res = null;
            final Class<?> responseType = restMethod.getResponseType();
            /*
            if (Reflections.isCollectionType(responseType)) {
                //TODO add 2020/02
                throw new UnsupportedOperationException(String.format("Not support collection type response '%s'",
                        responseType.getName()));
            }
            */
            String httpMethod = restMethod.getHttpMethod();
            switch (httpMethod) {
            case "GET":
                ret = client.doGet(url, responseType);
                break;
            case "POST":
                res = client.doPost(url, args[args.length-1]);
                if (res.hasEntity()) {
                    ret = res.readEntity(responseType);
                }
                break;
            case "PUT":
                res = client.doPut(url, args[args.length-1]);
                if (res.hasEntity()) {
                    ret = res.readEntity(responseType);
                }
                break;
            case "DELETE":
                res = client.doDelete(url, args[args.length-1]);
                if (res.hasEntity()) {
                    ret = res.readEntity(responseType);
                }
                break;
            }
            if (res != null) {
                final int status = res.getStatus();
                if(status >= 400) {
                    LOGGER.warning(String.format("[REST] %s '%s' - %s.%s() error : %d",
                            httpMethod, url.toString(),
                            source.getSimpleName(), method.getName(), status));
                } else {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine(String.format("[REST] %s '%s' %d - %s.%s()",
                            httpMethod, url.toString(), status,
                            source.getSimpleName(), method.getName()));
                    }
                }
            }
            return ret;
        }

    }
}