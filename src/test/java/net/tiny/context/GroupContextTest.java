package net.tiny.context;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


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

}