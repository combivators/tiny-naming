package net.tiny.message.test;

import java.util.logging.Logger;

import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.tiny.config.JsonParser;
import net.tiny.messae.api.Message;
import net.tiny.messae.api.MessageConsumer;

@Path("/v1/api/tc2")
public class TestConsumer2 implements MessageConsumer {

    private static final Logger LOGGER = Logger.getLogger(TestConsumer2.class.getName());
    @POST
    @Path("do")
    @Produces(MediaType.APPLICATION_JSON)
    public void accept(@BeanParam Message message) {
        LOGGER.info("[Consumer] /v1/api/tc2/do " + JsonParser.marshal(message));
    }
}
