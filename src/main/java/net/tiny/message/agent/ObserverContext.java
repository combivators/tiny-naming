package net.tiny.message.agent;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.tiny.context.RestWebServiceLocator;
import net.tiny.context.ServicePoint;
import net.tiny.messae.api.Message;
import net.tiny.messae.api.MessageConsumer;

public class ObserverContext implements Consumer<Message>, Predicate<Message> {

    private String endpoint;
    private transient MessageConsumer consumer;

    public ObserverContext() {}

    public ObserverContext(String endpoint) {
        setPoint(endpoint);
    }
    public String getPoint() {
        return endpoint;
    }
    public void setPoint(String endpoint) {
        this.endpoint = endpoint;
        ServicePoint point = ServicePoint.valueOf(endpoint);
        switch(point.getServiceType()) {
        case WS:
        case REST:
            final RestWebServiceLocator locator = new RestWebServiceLocator();
            this.consumer = locator.lookup(endpoint, MessageConsumer.class);
            break;
        case LOCAL:
               this.consumer = buildLoaclConsumer(point.getName());
            break;
        default:
            // TODO
            break;
        }
    }

    private MessageConsumer buildLoaclConsumer(String name) {
        try {
            return MessageConsumer.class.cast(Class.forName(name).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Consumer<Message> getConsumer() {
        return this;
    }

    public Predicate<Message> getFilter() {
        return this;
    }

    @Override
    public void accept(Message msg) {
        consumer.accept(msg);
    }

    @Override
    public boolean test(Message msg) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ObserverContext) {
            return endpoint.equals(((ObserverContext)obj).endpoint);
        }
        return false;
    }

    @Override
    public String toString() {
        return endpoint;
    }
}
