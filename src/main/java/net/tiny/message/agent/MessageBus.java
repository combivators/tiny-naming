package net.tiny.message.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.tiny.messae.api.Message;
import net.tiny.message.Bus;
import net.tiny.ws.Callback;

public class MessageBus {

    private static final Logger LOGGER = Logger.getLogger(MessageBus.class.getName());

    private Bus<Message> bus = Bus.getInstance(Message.class);
    private List<ConsumerContext> consumers = new ArrayList<>();
    private String token; //为空时检测无效

    public void register(String channel, ObserverContext observer) {
        List<ConsumerContext> list = findConsumersByEndpoint(observer.getPoint());
        if (list.isEmpty()) {
            ConsumerContext cc = new ConsumerContext();
            cc.getChannels().add(channel);
            cc.setEndpoint(observer.getPoint());
            consumers.add(cc);
        } else {
            for (ConsumerContext cc : list) {
                cc.getChannels().add(channel);
            }
        }

        Consumer<Callback<Message>> callback = null;
        bus.register(observer, channel, observer.getConsumer(), observer.getFilter(), callback);
        LOGGER.info(String.format("[BUS] register a consumer '%s' listen on '%s' channel.", observer.toString(), channel));
    }

    List<ConsumerContext> findConsumersByEndpoint(String point) {
        List<ConsumerContext> list = consumers.stream()
                .filter(c -> c.getEndpoint().equals(point))
                .collect(Collectors.toList());
        return list;
    }

    public List<ConsumerContext> findConsumers(String channel) {
        List<ConsumerContext> list = consumers.stream()
            .filter(c -> c.getChannels().contains(channel))
            .collect(Collectors.toList());
        Collections.unmodifiableList(list);
        LOGGER.info(String.format("[BUS] find %d consumer(s) of channel '%s'", list.size(), channel));
        return list;
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

    public boolean isValidChannel(String channel) {
        return bus.valid(channel);
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
        for (ConsumerContext cc : consumers) {
            for (String ch : cc.getChannels()) {
                register(ch, cc.getObserver());
            }
        }
    }
}
