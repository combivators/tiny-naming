package net.tiny.message.agent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.tiny.messae.api.Message;

public class ObserverContextTest {

    @Test
    public void testCompare() throws Exception {
        String sp1 = "http://192.168.1.100:8080/v1/api/msg";
        String sp2 = "http://192.168.1.100:8080/v1/api/msg";


        Message msg1 = new Message();
        msg1.setSource(sp1);
        ObserverContext oc1 = new ObserverContext(msg1.getSource());

        Message msg2 = new Message();
        msg2.setSource(sp2);
        ObserverContext oc2 = new ObserverContext(msg2.getSource());
        assertEquals(oc1, oc2);
        assertNotSame(oc1, oc2);
    }

}
