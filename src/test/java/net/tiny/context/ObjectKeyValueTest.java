package net.tiny.context;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

import org.junit.jupiter.api.Test;

public class ObjectKeyValueTest {

    @Test
    public void testGetterSetter() throws Exception {
        ObjectContext<String, String> oc = new ObjectKeyValue<String, String>();
        oc.setValue("1", "1");
        oc.setValue("1.1", "11");
        oc.setValue("1.1.1", "111");
        oc.setValue("1.1.1.1", "1111");
        oc.setValue("1.2", "12");
        oc.setValue("1.2.1", "121");
        oc.setValue("2", "2");
        oc.setValue("22", "22");

        assertTrue(oc.isReady());
        assertFalse(oc.hasException());
        assertEquals(ObjectContext.Status.READY, oc.getStatus());
        assertTrue(oc.isMarked());
        assertFalse(oc.isEmpty());
        assertEquals(8, oc.size());
        assertTrue(oc.existKey("1.1.1"));

        ObjectContext<String, String> ret = oc.find("1.[^.]*.1");
        assertEquals(2, ret.size());
        assertEquals("111", ret.getValue("1.1.1"));
        assertEquals("121", ret.getValue("1.2.1"));

        ret = oc.find("1.1.*");
        assertEquals(3, ret.size());
        assertEquals("11", ret.getValue("1.1"));
        assertEquals("111", ret.getValue("1.1.1"));
        assertEquals("1111", ret.getValue("1.1.1.1"));

        Collection<String> keys = oc.getKeys();
        assertEquals(8, keys.size());

        Collection<String> values = oc.getValues();
        assertEquals(8, values.size());

        assertEquals("111", oc.removeValue("1.1.1"));
        assertFalse(oc.existKey("1.1.1"));

        oc.clear();
        assertTrue(oc.isEmpty());
    }
}
