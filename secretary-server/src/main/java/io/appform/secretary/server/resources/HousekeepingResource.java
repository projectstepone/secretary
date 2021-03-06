package io.appform.secretary.server.resources;

import com.google.inject.Singleton;
import io.appform.secretary.model.GenericResponse;
import io.appform.secretary.model.Workflow;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.server.command.impl.FileDataDBCommand;
import io.appform.secretary.server.command.impl.WorkflowDBCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hibernate.validator.constraints.NotBlank;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Slf4j
@Path("v1/housekeeping")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Housekeeping APIs")
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@PermitAll
public class HousekeepingResource {

    private final FileDataDBCommand filedataCommand;
    private final WorkflowDBCommand workflowCommand;

    @GET
    @Path("/file/{userId}")
    @Operation(summary = "Get information about all files uploaded by a user")
    public Response getFilesByUser(@Valid @NotBlank @PathParam("userId") String userId) {
        log.info("Request: List files uploaded by user: {}", userId);

        val data = filedataCommand.getByUser(userId);

        log.info("Response: List of files uploaded by user {}: {}", userId, data);
        return Response.ok()
                .entity(GenericResponse.builder()
                        .success(true)
                        .data(data)
                        .build())
                .build();
    }

    @GET
    @Path("/workflow/{workflowId}")
    @Operation(summary = "Get information for a workflow")
    public Response getWorkflow(@Valid @NotBlank @PathParam("workflowId") String workflowId) {
        log.info("Request: Details for workflow: {}", workflowId);

        val data = workflowCommand.get(workflowId);

        if (!data.isPresent()) {
            throw new SecretaryError("Workflow is not present: " + workflowId,
                    ResponseCode.NOT_FOUND);
        }

        log.info("Response: Workflow details {}", data);
        return Response.ok()
                .entity(GenericResponse.builder()
                        .success(true)
                        .data(data)
                        .build())
                .build();
    }

    @GET
    @Path("/workflow")
    @Operation(summary = "Get information for a workflow")
    public Response getAllWorkflow(@QueryParam("active") boolean active) {
        log.info("Request: Get detail for all workflow");

        var data = workflowCommand.getAll();
        if (active) {
            data = data.stream()
                    .filter(Workflow::isEnabled)
                    .collect(Collectors.toList());
        }

        log.info("Response: List of workflow: {}", data);
        return Response.ok()
                .entity(GenericResponse.builder()
                        .success(true)
                        .data(data)
                        .build())
                .build();
    }
}
