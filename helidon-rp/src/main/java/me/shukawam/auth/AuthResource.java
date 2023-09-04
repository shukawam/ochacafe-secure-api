package me.shukawam.auth;

import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;

import io.helidon.security.SecurityContext;
import io.helidon.security.annotations.Authenticated;
import jakarta.json.Json;
import jakarta.json.JsonBuilderFactory;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("auth")
public class AuthResource {

    private static final Logger LOGGER = Logger.getLogger(AuthResource.class.getName());
    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    @GET
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response login(@Context SecurityContext securityContext, @Context ContainerRequestContext containerRequestContext) {
        LOGGER.info(securityContext.userName());
        return Response.ok().entity(
                JSON.createObjectBuilder()
                        .add("message", "Login Success!")
                        .add("access_token", getAccessToken(containerRequestContext))
                        .build())
                .build();
    }

    @GET
    @Path("logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout() {
        return Response.ok().entity(
            JSON.createObjectBuilder()
                .add("message", "Logout Success!")
                .build()
        ).build();
    }

     private String getAccessToken(ContainerRequestContext containerRequestContext) {
        var cookie = containerRequestContext.getCookies().get("JSESSIONID");
        if (Optional.ofNullable(cookie).isPresent()) {
            return cookie.getValue();
        } else {
            throw new RuntimeException("No cookie is existing.");
        }
    }

}
