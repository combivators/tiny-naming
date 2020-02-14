package net.tiny.message.agent;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.tiny.messae.api.Message;
import net.tiny.ws.rs.ApplicationException;

/**
 *
 * @see MessageService
 *
 */
@Path("/")
public class MessageAgent {

    @Resource
    private MessageBus bus;

    @POST
    @Path("api/v1/bus/{channel}/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void register(@PathParam("channel")String channel, @PathParam("token")String token, @BeanParam Message message) {
        if (!bus.isValidToken(token)) {
            throw new ApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        if (!bus.isValidChannel(channel)) {
            throw new ApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        if (message == null ) {
            throw new ApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        bus.register(channel, new ObserverContext(message.getSource()));
    }

    @GET
    @Path("api/v1/bus/{channel}/{token}/consumers")
    @Produces(value = MediaType.APPLICATION_JSON)
    public List<String> consumers(@PathParam("channel")String channel, @PathParam("token")String token) {
        if (!bus.isValidToken(token)) {
            throw new ApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        if (!bus.isValidChannel(channel)) {
            throw new ApplicationException(HttpURLConnection.HTTP_NOT_FOUND);
        }

        List<ConsumerContext> consumers = bus.findConsumers(channel);
        if (consumers.isEmpty()) {
            throw new ApplicationException(HttpURLConnection.HTTP_NOT_FOUND);
        }
        return consumers.stream()
            .map(c -> c.getEndpoint())
            .collect(Collectors.toList());
    }

    @GET
    @Path("api/v1/bus/{channel}/{token}/clear")
    public void clear(@PathParam("channel")String channel, @PathParam("token")String token) {
        if (!bus.isValidToken(token)) {
            throw new ApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        if (!bus.isValidChannel(channel)) {
            throw new ApplicationException(HttpURLConnection.HTTP_NOT_FOUND);
        }
        bus.clear(channel);
    }

    @DELETE
    @Path("api/v1/bus/{channel}/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void remove(@PathParam("channel")String channel, @PathParam("token")String token, @BeanParam Message message) {
        if (!bus.isValidToken(token)) {
            throw new ApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        if (!bus.isValidChannel(channel)) {
            throw new ApplicationException(HttpURLConnection.HTTP_NOT_FOUND);
        }
        if (message == null ) {
            throw new ApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        bus.remove(channel, new ObserverContext(message.getSource()));
    }

    @POST
    @Path("api/v1/msg/{channel}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void push(@PathParam("channel")String channel, @BeanParam Message message) {
        if (!bus.isValidChannel(channel)) {
            throw new ApplicationException(HttpURLConnection.HTTP_NOT_FOUND);
        }
        if (message == null ) {
            throw new ApplicationException(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        bus.publish(channel, message);
    }
}
