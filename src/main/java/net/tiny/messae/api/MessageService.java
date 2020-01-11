package net.tiny.messae.api;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface MessageService {

    @POST
    @Path("/v1/api/msg/reg/{channel}/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public void register(@PathParam("channel")String channel, @PathParam("token")String token, @BeanParam Message message);

    @POST
    @Path("/v1/api/msg/del/{channel}/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public void remove(@PathParam("channel")String channel, @PathParam("token")String token, @BeanParam Message message);

    @GET
    @Path("/v1/api/msg/clear/{channel}/{token}")
    public void clear(@PathParam("channel")String channel, @PathParam("token")String token);

    @POST
    @Path("/v1/api/msg/push/{channel}")
    @Produces(MediaType.APPLICATION_JSON)
    public void push(@PathParam("channel")String channel, @BeanParam Message message);
}
