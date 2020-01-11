package net.tiny.message.test;

import java.util.logging.Logger;

import net.tiny.config.JsonParser;
import net.tiny.messae.api.Message;
import net.tiny.messae.api.MessageConsumer;

public class LocalTestConsumer implements MessageConsumer {

    private static final Logger LOGGER = Logger.getLogger(LocalTestConsumer.class.getName());

    public void accept(Message message) {
        LOGGER.info("[Consumer] LocalTestConsumer#accept " + JsonParser.marshal(message));
    }
}
