package net.tiny.message.agent;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.tiny.config.JsonParser;

public class ConsumerContextTest {

    @Test
    public void testParse() {

        List<ConsumerContext> consumers = new ArrayList<>();
        ConsumerContext c1 = new ConsumerContext();
        c1.setChannels(Arrays.asList("ch1", "ch2"));
        c1.setEndpoint("http://localhost:8080/api/v1/tc1/do");

        ConsumerContext c2 = new ConsumerContext();
        c2.setChannels(Arrays.asList("ch2"));
        c2.setEndpoint("http://localhost:8080/api/v1/tc2/do");

        ConsumerContext c3 = new ConsumerContext();
        c3.setChannels(Arrays.asList("ch3"));
        c3.setEndpoint("LocalConsumer");

        consumers.add(c1);
        consumers.add(c2);
        consumers.add(c3);
        assertEquals(3, consumers.size());

        String json = JsonParser.marshal(consumers);
        System.out.println(json);
        assertEquals(195, json.length());

        List<?> list = JsonParser.unmarshal(json, List.class);
        assertEquals(3, list.size());
        System.out.println();
        for (Object o : list) {
            System.out.println(o.toString());
        }
    }
}
