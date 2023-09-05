package me.shukawam.auth;

import java.util.logging.Logger;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;

@Path("/api/cowsay")
public class CowwebResource {

    private static final Logger LOGGER = Logger.getLogger(CowwebResource.class.getName());
    private final CowwebRestClient client;
    
    @Inject
    public CowwebResource(@RestClient CowwebRestClient client) {
        this.client = client;
    }

    @GET
    @Path("/say")
    public String say(@Context ContainerRequestContext ctx) {
        return client.say();
    }

    @GET
    @Path("/think")
    public String think() {
        return client.think();
    }
}
