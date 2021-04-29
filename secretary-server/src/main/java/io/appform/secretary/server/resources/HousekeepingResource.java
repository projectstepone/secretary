package io.appform.secretary.server.resources;

import io.appform.secretary.model.FileData;
import io.appform.secretary.model.Workflow;
import io.appform.secretary.server.command.FileDataDBCommand;
import io.appform.secretary.server.command.WorkflowDBCommand;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Path("/v1/housekeeping")
@Produces(MediaType.APPLICATION_JSON)
@Api("Housekeeping APIs")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class HousekeepingResource {

    private final FileDataDBCommand filedataCommand;
    private final WorkflowDBCommand workflowCommand;

    @GET
    @Path("/file/{userId}")
    @ApiOperation("Get information about all files uploaded by a user")
    public Response getFilesByUser(@Valid @NotBlank @PathParam("userId") String userId) {
        log.info("Request: List files uploaded by user: {}", userId);

        List<FileData> data = filedataCommand.getByUser(userId);

        log.info("Response: List of files uploaded by user {}: {}", userId, data);
        return Response.ok().entity(data).build();
    }

    @GET
    @Path("/workflow/{workflowId}")
    @ApiOperation("Get information for a workflow")
    public Response getWorkflow(@Valid @NotBlank @PathParam("workflowId") String workflowId) {
        log.info("Request: Details for workflow: {}", workflowId);

        Optional<Workflow> data = workflowCommand.get(workflowId);

        if (!data.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        log.info("Response: Workflow details {}", data);
        return Response.ok().entity(data).build();
    }

    @GET
    @Path("/workflow")
    @ApiOperation("Get information for a workflow")
    public Response getAllWorkflow(@QueryParam("active") boolean active) {
        log.info("Request: Get detail for all workflow");

        List<Workflow> data = workflowCommand.getAll();
        if (active) {
            data = data.stream()
                    .filter(Workflow::isEnabled)
                    .collect(Collectors.toList());
        }

        log.info("Response: List of workflow: {}", data);
        return Response.ok().entity(data).build();
    }
}
