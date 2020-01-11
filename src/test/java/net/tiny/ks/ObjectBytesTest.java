package net.tiny.ks;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.zip.GZIPOutputStream;

import org.junit.jupiter.api.Test;

public class ObjectBytesTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testUncompress() throws Exception {
        ObjectContext<String, String> oc = new ObjectKeyValue<String, String>();
        oc.setValue("1", "1");
        oc.setValue("1.1", "11");
        oc.setValue("1.1.1", "111");
        oc.setValue("1.1.1.1", "1111");
        oc.setValue("1.2", "12");
        oc.setValue("1.2.1", "121");
        oc.setValue("2", "2");
        oc.setValue("22", "22");
        byte[] dat = ObjectBytes.toBytes(oc);

        System.out.println("toBytes : " + dat.length);// 469

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(baos);
        gzip.write(dat);
        gzip.finish();
        baos.flush();
        baos.close();

        byte[] gziped = baos.toByteArray();

        System.out.println("gziped : " + gziped.length); // 347

        String data = ObjectBytes.encode(oc, false);

        System.out.println("encode : " + data.length());// 631 System.out.
                                                        // println(data);

        Object obj = ObjectBytes.decode(data, false);
        assertNotNull(obj);

        assertTrue(obj instanceof ObjectContext);

        ObjectContext<String, String> ov = (ObjectContext<String, String>) obj;

        ObjectContext<String, String> ret = ov.find("1.[^.]*.1");
        assertEquals(2, ret.size());
        assertEquals("111", ret.getValue("1.1.1"));
        assertEquals("121", ret.getValue("1.2.1"));

        ret = ov.find("1.1.*");
        assertEquals(3, ret.size());
        assertEquals("11", ret.getValue("1.1"));
        assertEquals("111", ret.getValue("1.1.1"));
        assertEquals("1111", ret.getValue("1.1.1.1"));

        Collection<String> keys = ov.getKeys();
        assertEquals(8, keys.size());

        Collection<String> values = ov.getValues();
        assertEquals(8, values.size());
    }

    @SuppressWarnings("unchecked")
    public void testCompress() throws Exception {
        ObjectContext<String, String> oc = new ObjectKeyValue<String, String>();
        oc.setValue("1", "1");
        oc.setValue("1.1", "11");
        oc.setValue("1.1.1", "111");
        oc.setValue("1.1.1.1", "1111");
        oc.setValue("1.2", "12");
        oc.setValue("1.2.1", "121");
        oc.setValue("2", "2");
        oc.setValue("22", "22");
        String data = ObjectBytes.encode(oc);

        System.out.println("encode : " + data.length());//466
        System.out. println(data);

        Object obj = ObjectBytes.decode(data);
        assertNotNull(obj);

        assertTrue(obj instanceof ObjectContext);

        ObjectContext<String, String> ov = (ObjectContext<String, String>) obj;

        ObjectContext<String, String> ret = ov.find("1.[^.]*.1");
        assertEquals(2, ret.size());
        assertEquals("111", ret.getValue("1.1.1"));
        assertEquals("121", ret.getValue("1.2.1"));

        ret = ov.find("1.1.*");
        assertEquals(3, ret.size());
        assertEquals("11", ret.getValue("1.1"));
        assertEquals("111", ret.getValue("1.1.1"));
        assertEquals("1111", ret.getValue("1.1.1.1"));

        Collection<String> keys = ov.getKeys();
        assertEquals(8, keys.size());

        Collection<String> values = ov.getValues();
        assertEquals(8, values.size());
    }
}
