package net.tiny.message.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import net.tiny.messae.api.Message;
import net.tiny.message.Bus;
import net.tiny.ws.Callback;

public class MessageBus {

    private static final Logger LOGGER = Logger.getLogger(MessageBus.class.getName());

    private Bus<Message> bus = Bus.getInstance(Message.class);
    private List<ConsumerContext> consumers = new ArrayList<>();
    private String token; //为空时检测无效

    public void register(String channel, ObserverContext observer) {
        Consumer<Callback<Message>> callback = null;
        bus.register(observer, channel, observer.getConsumer(), observer.getFilter(), callback);
        LOGGER.info(String.format("[BUS] register a consumer '%s' listen on '%s' channel.", observer.toString(), channel));
    }

    public void publish(String channel, Message message) {
        bus.publish(channel, message);
    }

    public void remove(String channel, ObserverContext observer) {
        bus.remove(channel, observer);
        LOGGER.info(String.format("[BUS] remove a consumer '%s' from '%s' channel.", observer.toString(), channel));
    }

    public void clear(String channel) {
        bus.clear(channel);
    }

    public boolean isValidToken(String target) {
        //为空时检测无效
        if (token == null)
            return true;
        return token.equals(target);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<ConsumerContext> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<ConsumerContext> consumers) {
        this.consumers =consumers;
        for (ConsumerContext cc : consumers) {
            for (String ch : cc.getChannels()) {
                register(ch, cc.getObserver());
            }
        }
    }
}
