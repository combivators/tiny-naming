package net.tiny.ks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.tiny.context.ServicePoint;


public class GroupContextTest {

    @Test
    public void testGetterSetter() throws Exception {
        GroupContext<String> gc =  new GroupContext<>("1.1.1");
        assertEquals("1.1.1", gc.getCurrent());
        assertEquals(gc.getCurrent(), gc.getFrist());
        assertEquals(gc.getCurrent(), gc.getLast());
        assertEquals(1, gc.size());

        gc.append("1.1.2").append("1.1.3").append("1.2.1");
        assertEquals(4, gc.size());
        assertEquals("1.1.1", gc.getCurrent());
        assertEquals("1.1.2", gc.getNext());
        assertEquals("1.1.2", gc.next());
        assertEquals("1.1.2", gc.getCurrent());
        System.out.println(gc.toString());

        assertEquals(2, gc.indexOf("1.1.3"));
        String[] array = gc.toArray(new String[gc.size()]);
        assertEquals(4, array.length);

        String c = gc.next();
        assertEquals("1.1.3", c);
        gc = gc.remove(c);
        assertEquals("1.1.1", gc.getCurrent());
        gc = gc.remove("1.2.1").remove("1.1.2");
        assertEquals(1, gc.size());

        gc.clear();
    }

    @Test
    public void testGetFirstNextLast() throws Exception {

        ServicePoint sc = new ServicePoint();
        assertEquals("Endpoint", sc.getName());
        assertEquals("localhost", sc.getHost());
        assertEquals(80, sc.getPort());

        GroupContext<ServicePoint> gc = new GroupContext<ServicePoint>(sc);

        ServicePoint curr = gc.getCurrent();

        ServicePoint frist = gc.getFrist();
        assertEquals(0, sc.compareTo(frist));
        assertEquals(sc, frist);
        assertEquals(curr, frist);

        gc.append(ServicePoint.valueOf("Endpoint"));
        gc.append(ServicePoint.valueOf("http://172.30.100.10/endpoint"));
        gc.append(ServicePoint.valueOf("http://172.30.100.10:1100/endpoint"));
        gc.append(ServicePoint.valueOf("http://172.30.100.11/endpoint"));
        gc.append(ServicePoint.valueOf("http://172.30.110.10/endpoint"));
        gc.append(ServicePoint.valueOf("http://128.10.100.10/endpoint"));
        gc.append(ServicePoint.valueOf("http://128.10.100.10:1100/endpoint"));
        gc.append(ServicePoint.valueOf("http://128.50.100.10/endpoint"));
        gc.append(ServicePoint.valueOf("http://128.30.100.10/endpoint"));
        gc.append(ServicePoint.valueOf("http://128.30.100.10:1099/endpoint"));
        gc.append(ServicePoint.valueOf("http://128.60.100.10/endpoint"));


        System.out.println(gc.toString());
        assertEquals(12, gc.size());

        ServicePoint[] array = gc.toArray(new ServicePoint[gc.size()]);
        assertEquals(12, array.length);

        int idx = gc.indexOf(sc);
        assertEquals(1, idx);

        ServicePoint last = gc.getLast();
        assertEquals("172.30.110.10", last.getHost());
        assertEquals(80, last.getPort());

        frist = gc.getFrist();
        assertEquals("localhost", frist.getHost());
        assertEquals(-1, frist.getPort());

        curr = gc.getCurrent();
        assertEquals("localhost", curr.getHost());
        assertEquals(80, curr.getPort());

        ServicePoint next = gc.getNext();
        assertEquals("128.10.100.10", next.getHost());
        assertEquals(80, next.getPort());
        next = gc.getNext();
        assertEquals("128.10.100.10", next.getHost());
        assertEquals(80, next.getPort());

        next = gc.getNext(next);

        assertEquals("128.10.100.10", next.getHost());
        assertEquals(1100, next.getPort());

        gc = new GroupContext<ServicePoint>(ServicePoint.valueOf("http://172.30.100.10:1100/v1/api/{token}") );
        gc.append(new ServicePoint());

        System.out.println(gc.toString());
        assertEquals(2, gc.size());

        gc.remove(ServicePoint.valueOf("http://172.30.100.10:1100/v1/api/{token}"));
        assertEquals(1, gc.size());
        curr = gc.getCurrent();
        assertEquals("localhost", sc.getHost());
        assertEquals(80, sc.getPort());

        gc.remove(new ServicePoint());
        assertEquals(0, gc.size());

        // current is null
        gc.remove(ServicePoint.valueOf("http://172.30.100.10:1100/v1/api/{token}"));
        assertNull(gc.getFrist());
        assertNull(gc.getLast());
        assertNull(gc.getNext());

        assertNull(gc.getNext(new ServicePoint()));

        array = gc.toArray(new ServicePoint[gc.size()]);
        assertEquals(0, array.length);
    }

}