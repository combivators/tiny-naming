package net.tiny.message.agent;

import java.util.ArrayList;
import java.util.List;

public class ConsumerContext {

    private String endpoint;
    private List<String> channels = new ArrayList<>();
    private transient ObserverContext observer;

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public ObserverContext getObserver() {
        if (null == observer) {
            observer = new ObserverContext(endpoint);
        }
        return observer;
    }
}
