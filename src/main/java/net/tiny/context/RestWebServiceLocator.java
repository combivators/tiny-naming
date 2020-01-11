package net.tiny.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;

import net.tiny.ws.rs.client.RestClient;
import net.tiny.ws.rs.client.RestMethod;

public class RestWebServiceLocator implements ServiceFeature {

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

        //private final Class<?> type;
        private final ServicePoint point;
        private final RestClient client;

        private <T> ProxyHandler(Class<T> type, ServicePoint point) {
            this.point = point;
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
            RestClient.Response res;
            final Class<?> responseType = restMethod.getResponseType();
            switch (restMethod.getHttpMethod()) {
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
                res = client.doDelete(url);
                if (res.hasEntity()) {
                    ret = res.readEntity(responseType);
                }
                break;
            }
            return ret;
        }

    }
}