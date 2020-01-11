package net.tiny.message.agent;

import java.net.HttpURLConnection;

import javax.annotation.Resource;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.tiny.messae.api.Message;
import net.tiny.ws.rs.ApplicationException;

@Path("/v1/api/msg")
public class MessageAgent {

    @Resource
    private MessageBus bus;

    @POST
    @Path("reg/{channel}/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public void register(@PathParam("channel")String channel, @PathParam("token")String token, @BeanParam Message message) {
        if (!bus.isValidToken(token)) {
            throw new ApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        bus.register(channel, new ObserverContext(message.getSource()));
    }

    @POST
    @Path("del/{channel}/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public void remove(@PathParam("channel")String channel, @PathParam("token")String token, @BeanParam Message message) {
        if (!bus.isValidToken(token)) {
            throw new ApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        bus.remove(channel, new ObserverContext(message.getSource()));
    }

    @GET
    @Path("clear/{channel}/{token}")
    public void clear(@PathParam("channel")String channel, @PathParam("token")String token) {
        if (!bus.isValidToken(token)) {
            throw new ApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        bus.clear(channel);
    }

    @POST
    @Path("push/{channel}")
    @Produces(MediaType.APPLICATION_JSON)
    public void push(@PathParam("channel")String channel, @BeanParam Message message) {
        bus.publish(channel, message);
    }
}
