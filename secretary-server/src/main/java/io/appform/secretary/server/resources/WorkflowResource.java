package io.appform.secretary.server.resources;

import com.google.inject.Singleton;
import io.appform.secretary.model.GenericResponse;
import io.appform.secretary.model.Workflow;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.model.workflow.WorkflowCreateRequest;
import io.appform.secretary.model.workflow.WorkflowUpdateRequest;
import io.appform.secretary.server.command.WorkflowProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@Path("v1/workflow")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Workflow APIs")
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class WorkflowResource {

    private final WorkflowProvider dbCommand;

    @POST
    @Path("/create")
    @Operation(summary = "Create and enable a new workflow")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createWorkflow(@Valid WorkflowCreateRequest request) {
        log.info("Request: Create workflow: {}", request);

        val getWorkflow = dbCommand.get(request.getWorkflow());
        if (getWorkflow.isPresent()) {
            throw new SecretaryError("Workflow is already present: " + request.getWorkflow(),
                    ResponseCode.BAD_REQUEST);
        }

        val optionalWorkflow = dbCommand.save(Workflow.builder()
                .name(request.getWorkflow())
                .enabled(true)
                .build());

        if (!optionalWorkflow.isPresent()) {
            throw new SecretaryError("Unable to create workflow: " + request.getWorkflow(),
                    ResponseCode.INTERNAL_SERVER_ERROR);
        }

        val workflow = optionalWorkflow.get();
        log.info("Response: Created workflow: {}", workflow);
        return Response.ok()
                .entity(GenericResponse.builder()
                        .success(true)
                        .data(workflow)
                        .build())
                .build();
    }

    @PUT
    @Path("/update")
    @Operation(summary = "Update workflow")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateWorkflow(@Valid WorkflowUpdateRequest request) {
        log.info("Request: Update workflow: {}", request);

        val getWorkflow = dbCommand.get(request.getWorkflow());
        if (!getWorkflow.isPresent()) {
            throw new SecretaryError("Workflow is not present: " + request.getWorkflow(),
                    ResponseCode.NOT_FOUND);
        }

        // Possible TICTOU issue
        val updateWorkflow = dbCommand.update(Workflow.builder()
                .name(request.getWorkflow())
                .enabled(request.isEnabled())
                .build());

        if (!updateWorkflow.isPresent()) {
            throw new SecretaryError("Unable to update workflow: " + request.getWorkflow(),
                ResponseCode.NOT_FOUND);
        }

        val workflow = updateWorkflow.get();
        log.info("Response: Updated workflow: {}", workflow);
        return Response.ok()
                .entity(GenericResponse.builder()
                        .success(true)
                        .data(workflow)
                        .build())
                .build();
    }

}
