package net.tiny.context;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.LogManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.tiny.service.ServiceLocator;

public class ServiceFeatureLocatorTest {

    @BeforeAll
    public static void beforeAll() throws Exception {
        LogManager.getLogManager()
            .readConfiguration(Thread.currentThread().getContextClassLoader().getResourceAsStream("logging.properties"));
    }

    @Test
    public void testServiceFeatureLocator() throws Exception {
        final Object main = new DummyMain();
        final Object config = new DummyConfig();
        final Integer pid = 100;

        final ServiceLocator.ServiceMonitor monitor = new ServiceLocator.ServiceMonitor();
        final ServiceLocator serviceContext = new ServiceLocator();
        serviceContext.setListener(monitor);

        final Map<String, Object> collection = new HashMap<>();
        collection.put("local", new LocalServiceLocator(serviceContext));
        //collection.put("iiop", new IIOPServiceLocator());
        collection.put("rmi", new RMIServiceLocator());
        final DummyServiceBean dsb = new DummyServiceBean();
        collection.put("DummyService", dsb);

        final Callable<Properties> callable = new Callable<Properties>() {
            @Override
            public Properties call() {
                // Setup service locator properties;
                Properties services = new Properties();
                services.put("config", config);
                services.put("main", main);
                services.put("PID", pid);
                for (String key : collection.keySet()) {
                    services.put(key, collection.get(key));
                }
                return services;
            }
        };


        serviceContext.accept(callable);

        assertTrue(serviceContext.exist("PID"));
        assertTrue(serviceContext.exist("main"));
        assertTrue(serviceContext.exist("config"));
        assertTrue(serviceContext.exist("DummyService"));
        DummyService ds = serviceContext.lookup(DummyService.class);
        assertNotNull(ds);
        assertEquals(dsb.hashCode(), ds.hashCode());


        Object bean = serviceContext.lookup("DummyService");
        assertTrue(bean instanceof DummyServiceBean);
        assertEquals(ds.hashCode(), bean.hashCode());

        ServiceFeatureLocator locator = new ServiceFeatureLocator();
        locator.setServiceContext(serviceContext);
        locator.setFeatures(Arrays.asList("local", "rmi"));

        ServiceFeature feature = locator.feature(ServiceType.LOCAL);
        assertNotNull(feature);
        assertTrue(feature instanceof LocalServiceLocator);
        //assertTrue(locator.feature(ServiceType.IIOP) instanceof IIOPServiceLocator);
        assertTrue(locator.feature(ServiceType.RMI) instanceof RMIServiceLocator);

        //Local
        ds = locator.lookup("DummyService", DummyService.class);
        assertNotNull(ds);
        assertEquals(dsb.hashCode(), ds.hashCode());
    }


    static class DummyMain {}
    static class DummyConfig {}

    static interface DummyService {}
    static class DummyServiceBean implements DummyService {}

}
