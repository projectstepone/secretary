package io.appform.secretary.resources;

import io.appform.secretary.command.FileDataDBCommand;
import io.appform.secretary.model.FileData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Slf4j
@Path("/v1/housekeeping")
@Produces(MediaType.APPLICATION_JSON)
@Api("Housekeeping APIs")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class Housekeeping {

    private final FileDataDBCommand dbCommand;

    @GET
    @Path("/file/{userId}")
    @ApiOperation("Get information about all files uploaded by a user")
    public Response getFilesByUser(@Valid @NotBlank @PathParam("userId") String userId) {
        log.info("Request: List files uploaded by user: {}", userId);

        List<FileData> data = dbCommand.getByUser(userId);

        log.info("Response: List of files uploaded by user {}: {}", userId, data);
        return Response.ok().entity(data).build();
    }
}
