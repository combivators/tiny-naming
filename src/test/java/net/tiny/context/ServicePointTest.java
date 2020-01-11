package net.tiny.context;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URL;

import org.junit.jupiter.api.Test;

public class ServicePointTest {

    @Test
    public void testIsValidEndpoint() throws Exception {
        assertTrue(ServicePoint.isValid("http://192.168.100.101:8080/endpoint"));
        assertTrue(ServicePoint.isValid("rmi://192.168.100.101:1099/endpoint"));
        assertTrue(ServicePoint.isValid("https://localhost/endpoint"));
        assertTrue(ServicePoint.isValid("http://api.bus.net:8080/endpoint"));
        assertTrue(ServicePoint.isValid("iiop://localhost/endpoint"));
        assertFalse(ServicePoint.isValid("ftp://localhost/endpoint"));
    }

    @Test
    public void testParseEndpoint() throws Exception {
        String[] endpoint = ServicePoint.parseEndpoint("http://192.168.100.101:8080/endpoint");
        assertEquals("http", endpoint[0]);
        assertNull(endpoint[1]);
        assertNull(endpoint[2]);
        assertEquals("192.168.100.101", endpoint[3]);
        assertEquals("8080", endpoint[4]);
        assertEquals("endpoint", endpoint[5]);

        endpoint = ServicePoint.parseEndpoint("https://api.bus.net/api/do?t=abc&c=ch1");
        assertEquals("https", endpoint[0]);
        assertNull(endpoint[1]);
        assertNull(endpoint[2]);
        assertEquals("api.bus.net", endpoint[3]);
        assertEquals("443", endpoint[4]);
        assertEquals("api/do?t=abc&c=ch1", endpoint[5]);
    }

    @Test
    public void testParseEndpointWithCredentials() throws Exception {
        String[] endpoint = ServicePoint.parseEndpoint("http://user:password@192.168.100.101:8080/endpoint");
        assertEquals("http", endpoint[0]);
        assertEquals("user", endpoint[1]);
        assertEquals("password", endpoint[2]);
        assertEquals("192.168.100.101", endpoint[3]);
        assertEquals("8080", endpoint[4]);
        assertEquals("endpoint", endpoint[5]);

        endpoint = ServicePoint.parseEndpoint("https://user:password@api.bus.net/api/do?t=abc&c=ch1");
        assertEquals("https", endpoint[0]);
        assertEquals("user", endpoint[1]);
        assertEquals("password", endpoint[2]);
        assertEquals("api.bus.net", endpoint[3]);
        assertEquals("443", endpoint[4]);
        assertEquals("api/do?t=abc&c=ch1", endpoint[5]);

        endpoint = ServicePoint.parseEndpoint("https://unknow@api.bus.net/api/do?t=abc&c=ch1");
        assertEquals("https", endpoint[0]);
        assertNull(endpoint[1]);
        assertNull(endpoint[2]);
        assertEquals("api.bus.net", endpoint[3]);
        assertEquals("443", endpoint[4]);
        assertEquals("api/do?t=abc&c=ch1", endpoint[5]);
    }

    @Test
    public void testParseEndpointWithPattern() throws Exception {
        String[] endpoint = ServicePoint.parseEndpoint("https://api.bus.net/api/do?t={token}&c={ch}");
        assertEquals("https", endpoint[0]);
        assertEquals("api.bus.net", endpoint[3]);
        assertEquals("443", endpoint[4]);
        assertEquals("api/do?t={token}&c={ch}", endpoint[5]);

    }


    @Test
    public void testCompareHost() throws Exception {

        ServicePoint sc = new ServicePoint();

        int ret = sc.compareHost("128.50.100.10", "128.50.100.10");

        assertEquals(0, ret);
        ret = sc.compareHost("128.10.100.10", "128.50.100.10");
        assertEquals(9, ret);
        ret = sc.compareHost("128.30.100.10", "128.50.100.10");
        assertEquals(9, ret);
        ret = sc.compareHost("128.10.100.10", "128.50.100.10", "128.50.100.10");
        assertEquals(1, ret);

        ret = sc.compareHost("128.10.100.10", "128.30.100.10", "128.50.100.10");
        assertEquals(-2, ret);

    }

    @Test
    public void testEndpoint() throws Exception {
        URL url = new URL("http://192.168.1.200:8080/v1/api/msg/{ch}?t={token}");
        ServicePoint point = new ServicePoint(url);
        assertEquals("192.168.1.200", point.getHost());
        assertEquals(8080, point.getPort());
        assertEquals("/v1/api/msg/{ch}?t={token}", point.getPath());

        url = new URL("http://localhost/v1/api/msg/{ch}/{id}");
        point = new ServicePoint(url);
        assertEquals("localhost", point.getHost());
        assertEquals(80, point.getPort());
        assertEquals("/v1/api/msg/{ch}/{id}", point.getPath());
    }
}