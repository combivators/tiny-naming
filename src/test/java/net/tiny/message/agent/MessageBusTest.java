package net.tiny.message.agent;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;


public class MessageBusTest {

    @Test
    public void testMessageBus() {
        MessageBus bus = new MessageBus();

        ConsumerContext c1 = new ConsumerContext();
        c1.setChannels(Arrays.asList("ch1", "ch2"));
        c1.setEndpoint("http://localhost:8080/api/v1/tc1/do");
        bus.register("ch1", new ObserverContext(c1.getEndpoint()));
        bus.register("ch2", new ObserverContext(c1.getEndpoint()));

        ConsumerContext c2 = new ConsumerContext();
        c2.setChannels(Arrays.asList("ch2"));
        c2.setEndpoint("http://localhost:8080/api/v1/tc2/do");
        bus.register("ch2", new ObserverContext(c2.getEndpoint()));

        ConsumerContext c3 = new ConsumerContext();
        c3.setChannels(Arrays.asList("ch3"));
        c3.setEndpoint("net.tiny.message.test.LocalTestConsumer");
        bus.register("ch3", new ObserverContext(c3.getEndpoint()));

        List<ConsumerContext> consumers = bus.findConsumers("ch2");
        assertEquals(2, consumers.size());
    }

    @Test
    public void testSetConsumers() {
        MessageBus bus = new MessageBus();

        ConsumerContext c1 = new ConsumerContext();
        c1.setChannels(Arrays.asList("ch1", "ch2"));
        c1.setEndpoint("http://localhost:8080/api/v1/tc1/do");

        ConsumerContext c2 = new ConsumerContext();
        c2.setChannels(Arrays.asList("ch2"));
        c2.setEndpoint("http://localhost:8080/api/v1/tc2/do");

        ConsumerContext c3 = new ConsumerContext();
        c3.setChannels(Arrays.asList("ch3"));
        c3.setEndpoint("net.tiny.message.test.LocalTestConsumer");
        List<ConsumerContext> consumers = new ArrayList<>();
        consumers.add(c1);
        consumers.add(c2);
        consumers.add(c3);

        bus.setConsumers(consumers);
        List<ConsumerContext> list = bus.findConsumers("ch2");
        assertEquals(2, list.size());
    }
}
