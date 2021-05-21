package io.appform.secretary.server.resources;

import io.appform.secretary.model.GenericResponse;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.model.schema.cell.request.CreateRequest;
import io.appform.secretary.model.schema.cell.request.UpdateRequest;
import io.appform.secretary.server.command.CellSchemaProvider;
import io.appform.secretary.server.translator.request.CellSchemaTranslator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hibernate.validator.constraints.NotBlank;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
@Path("/v1/schema/cell")
@Produces(MediaType.APPLICATION_JSON)
@Api("Cell Schema APIs")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CellSchemaResource {

    private final CellSchemaTranslator translator;
    private final CellSchemaProvider schemaProvider;

    @GET
    @ApiOperation("Get all schemas")
    public Response getAllSchema(@QueryParam("active") boolean active) {
        log.info("Request: Get detail for all schemas");
        List<CellSchema> schemas = schemaProvider.getAll();
        if (active) {
            schemas = schemas.stream()
                    .filter(CellSchema::isActive)
                    .collect(Collectors.toList());
        }

        log.info("Response: List of schemas: {}", schemas);
        return Response.ok()
                .entity(schemas)
                .build();
    }

    @GET
    @Path("/{schemaId}")
    @ApiOperation("Get schema for given ID")
    public Response getSchema(@PathParam("schemaId") @Valid @NotBlank final String schemaId) {
        log.info("Request: Details for schema for uuid : {}", schemaId);

        val uuid = schemaId.trim();
        val schema = schemaProvider.get(uuid);

        if (schema.isPresent()) {
            log.info("Response: Schema details: {}", schema.get());
            return Response.ok()
                    .entity(GenericResponse.builder()
                            .success(true)
                            .data(schema.get())
                            .build())
                    .build();
        } else {
            throw new SecretaryError("Unable to find schema: " + uuid,
                    ResponseCode.BAD_REQUEST);
        }
    }

    @POST
    @Path("/create")
    @ApiOperation("Create and enable a new schema")
    public Response createSchema(@Valid CreateRequest request) {
        log.info("Request: Create schema: {}", request);

        //TODO: Filter instance of abstract class
        //TODO: Add validator for request
        val schema = translator.toSchema(request);
        val newSchema = schemaProvider.save(schema);
        if (!newSchema.isPresent()) {
            throw new SecretaryError("Unable to create schema: " + request,
                    ResponseCode.INTERNAL_SERVER_ERROR);
        }

        log.info("Response: Created schema: {}", newSchema.get());
        return Response.ok()
                .entity(newSchema.get())
                .build();
    }

    @PUT
    @Path("/update/{schemaId}")
    @ApiOperation("Update schema")
    public Response updateSchema(@PathParam("schemaId") String schemaId,
                                 @Valid UpdateRequest request) {
        log.info("Request: Update schema: uuid {} to {}", schemaId, request);

        val schema = schemaProvider.get(schemaId);
        if (!schema.isPresent()) {
            throw new SecretaryError("Schema is not present: " + schemaId,
                    ResponseCode.BAD_REQUEST);
        }

        val newSchema = schemaProvider.update(translator.toSchema(request, schema.get()));
        if (!newSchema.isPresent()) {
            throw new SecretaryError("Unable to update schema: " + request,
                    ResponseCode.INTERNAL_SERVER_ERROR);
        }

        log.info("Response: Updated schema: {}", newSchema.get());
        return Response.ok()
                .entity(GenericResponse.builder()
                        .success(true)
                        .data(newSchema.get())
                        .build())
                .build();
    }

}
