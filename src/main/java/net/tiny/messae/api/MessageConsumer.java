package net.tiny.messae.api;

import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface MessageConsumer {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void accept(@BeanParam Message message);
}
