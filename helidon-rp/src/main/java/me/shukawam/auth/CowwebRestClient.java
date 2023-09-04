package me.shukawam.auth;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/cowsay")
@RegisterRestClient
@RegisterClientHeaders
public interface CowwebRestClient {

    @GET
    @Path("/say")
    public String say();

    @GET
    @Path("/think")
    public String think();
}
