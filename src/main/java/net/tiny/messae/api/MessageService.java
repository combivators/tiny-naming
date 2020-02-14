package net.tiny.messae.api;

import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface MessageService {

    @POST
    @Path("api/v1/bus/{channel}/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void register(@PathParam("channel")String channel, @PathParam("token")String token, @BeanParam Message message);

    @GET
    @Path("api/v1/bus/{channel}/{token}/consumers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> consumers(@PathParam("channel")String channel, @PathParam("token")String token);

    @GET
    @Path("api/v1/bus/{channel}/{token}/clear")
    public void clear(@PathParam("channel")String channel, @PathParam("token")String token);

    @DELETE
    @Path("api/v1/bus/{channel}/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void remove(@PathParam("channel")String channel, @PathParam("token")String token, @BeanParam Message message);

    @POST
    @Path("api/v1/msg/{channel}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void push(@PathParam("channel")String channel, @BeanParam Message message);
}
