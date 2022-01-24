package io.appform.secretary.server.resources;

import com.google.inject.Singleton;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/")
@Singleton
@Tag(name = "Home page")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class HomeResource {

    @GET
    @Operation(summary = "Redirects root to console view")
    public Response home() {
        return Response.seeOther(URI.create("/v1/console")).build();
    }
}
