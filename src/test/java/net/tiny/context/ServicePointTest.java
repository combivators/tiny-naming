package net.tiny.context;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ServicePointTest {

    @Test
    public void testIsVaildAddress() throws Exception {

        assertTrue(ServicePoint.isValid("192.168.100.101"));
        assertTrue(ServicePoint.isValid("192.168.10.1"));
        assertTrue(ServicePoint.isValid("128.50.10.11"));
        assertTrue(ServicePoint.isValid("192.168.10.1:1099"));
        assertTrue(ServicePoint.isValid("1099"));
        assertTrue(ServicePoint.isValid("localhost"));
        assertTrue(ServicePoint.isValid("PC110084"));
        assertTrue(ServicePoint.isValid("PC110084_12:1099"));
        assertTrue(ServicePoint.isValid("PC110084-A1:1099"));
        assertTrue(ServicePoint.isValid("localhost:1099"));

        assertFalse(ServicePoint.isValid("192.168.10.1234"));
        assertFalse(ServicePoint.isValid("1921.168.10.123"));
        assertFalse(ServicePoint.isValid("192.168.10.12.34"));
        assertFalse(ServicePoint.isValid("192.168.10"));
        assertFalse(ServicePoint.isValid("192.168.al.1"));
        assertFalse(ServicePoint.isValid("192.168.10.1:1a99"));
        assertFalse(ServicePoint.isValid("192.168.10.1:110084"));
        assertFalse(ServicePoint.isValid("110084"));
        assertFalse(ServicePoint.isValid("1a99"));
        assertFalse(ServicePoint.isValid(":1099"));
        assertFalse(ServicePoint.isValid("PC110084#12:1099"));
    }

    public void testParseAddress() throws Exception {

        String[] address = ServicePoint.parseAddress("192.168.100.101", 1099);
        assertEquals("192.168.100.101", address[0]);
        assertEquals("1099", address[1]);

        address = ServicePoint.parseAddress("192.168.10.1:1100", 1099);
        assertEquals("192.168.10.1", address[0]);
        assertEquals("1100", address[1]);

        address = ServicePoint.parseAddress("7703", 1099);
        assertEquals("localhost", address[0]);
        assertEquals("7703", address[1]);

        address = ServicePoint.parseAddress("PC110084", 1099);
        assertEquals("PC110084", address[0]);
        assertEquals("1099", address[1]);

        address = ServicePoint.parseAddress("PC110084-A1:7703", 1099);
        assertEquals("PC110084-A1", address[0]);
        assertEquals("7703", address[1]);
    }


    @Test
    public void testGetSet() throws Exception {

        ServicePoint sc = new ServicePoint();
        assertEquals(ServicePoint.DEFAULT_NAME, sc.getName());
        assertEquals("localhost", sc.getHost());
        assertEquals(-1, sc.getPort());

        GroupContext<ServicePoint> gc = new GroupContext<ServicePoint>(sc);

        ServicePoint curr = gc.getCurrent();

        ServicePoint frist = gc.getFrist();
        assertEquals(0, sc.compareTo(frist));
        assertEquals(sc, frist);
        assertEquals(curr, frist);

        gc.append(new ServicePoint(1098));
        gc.append(new ServicePoint("192.168.100.10"));
        gc.append(new ServicePoint("192.168.100.10", 1100));
        gc.append(new ServicePoint("192.168.100.11"));
        gc.append(new ServicePoint("192.168.110.10"));
        gc.append(new ServicePoint("128.10.100.10"));
        gc.append(new ServicePoint("128.10.100.10", 1100));
        gc.append(new ServicePoint("128.50.100.10"));
        gc.append(new ServicePoint("128.30.100.10"));
        gc.append(new ServicePoint("128.30.100.10", 1099));
        gc.append(new ServicePoint("128.60.100.10"));

        System.out.println(gc.toString());
        assertEquals(12, gc.size());

        ServicePoint[] array = gc.toArray(new ServicePoint[gc.size()]);
        assertEquals(12, array.length);

        int idx = gc.indexOf(sc);
        assertEquals(0, idx);

        ServicePoint last = gc.getLast();
        assertEquals("192.168.110.10", last.getHost());
        assertEquals(-1, last.getPort());

        ServicePoint next = gc.getNext();
        assertEquals("localhost", next.getHost());
        assertEquals(1098, next.getPort());

        next = gc.getNext(next);

        assertEquals("128.10.100.10", next.getHost());
        assertEquals(-1, next.getPort());

        gc = new GroupContext<ServicePoint>(new ServicePoint("192.168.100.10", 1100));
        gc.append(new ServicePoint());

        System.out.println(gc.toString());
        assertEquals(2, gc.size());

        gc.remove(new ServicePoint("192.168.100.10", 1100));
        assertEquals(1, gc.size());
        curr = gc.getCurrent();
        assertEquals("localhost", sc.getHost());
        assertEquals(-1, sc.getPort());

        gc.remove(new ServicePoint());
        assertEquals(0, gc.size());

        // current is null
        gc.remove(new ServicePoint("192.168.100.10", 1100));
        assertNull(gc.getFrist());
        assertNull(gc.getLast());
        assertNull(gc.getNext());

        assertNull(gc.getNext(new ServicePoint()));

        array = gc.toArray(new ServicePoint[gc.size()]);
        assertEquals(0, array.length);

        sc = new ServicePoint();
        sc.setAddress("192.168.100.10:1100");
        assertEquals("192.168.100.10", sc.getHost());
        assertEquals(1100, sc.getPort());
    }

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
}