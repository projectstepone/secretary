package io.appform.secretary.resources;

import io.appform.secretary.command.WorkflowProvider;
import io.appform.secretary.model.Workflow;
import io.appform.secretary.model.workflow.WorkflowCreateRequest;
import io.appform.secretary.model.workflow.WorkflowUpdateRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Slf4j
@Path("/v1/workflow")
@Produces(MediaType.APPLICATION_JSON)
@Api("Workflow APIs")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class WorkflowResource {

    private final WorkflowProvider dbCommand;

    @POST
    @Path("/create")
    @ApiParam("Create and enable a new workflow")
    public Response createWorkflow(@Valid WorkflowCreateRequest request) {
        log.info("Request: Create workflow: {}", request);

        Optional<Workflow> optionalWorkflow = dbCommand.save(Workflow.builder()
                .name(request.getWorkflow())
                .enabled(true)
                .build());

        if (!optionalWorkflow.isPresent()) {
            //TODO: Raise appropriate exception
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        Workflow workflow = optionalWorkflow.get();
        log.info("Response: Created workflow: {}", workflow);
        return Response.ok().entity(workflow).build();
    }

    @PUT
    @Path("/update")
    @ApiParam("Update workflow")
    public Response updateWorkflow(@Valid WorkflowUpdateRequest request) {
        log.info("Request: Update workflow: {}", request);

        // Possible TICTOU issue
        Optional<Workflow> getWorkflow = dbCommand.get(request.getWorkflow());
        if (!getWorkflow.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Optional<Workflow> updateWorkflow = dbCommand.update(Workflow.builder()
                .name(request.getWorkflow())
                .enabled(request.isEnabled())
                .build());

        if (!updateWorkflow.isPresent()) {
            //TODO: Raise appropriate exception
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        Workflow workflow = updateWorkflow.get();
        log.info("Response: Updated workflow: {}", workflow);
        return Response.ok().entity(workflow).build();
    }

}
