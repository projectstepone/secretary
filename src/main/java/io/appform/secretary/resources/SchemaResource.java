package io.appform.secretary.resources;

import io.appform.secretary.command.ValidationSchemaDBCommand;
import io.appform.secretary.model.ValidationSchema;
import io.appform.secretary.model.validationschema.NewSchemaRequest;
import io.appform.secretary.model.validationschema.UpdateSchemaRequest;
import io.appform.secretary.utils.ValidationSchemaUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
@Path("/v1/schema")
@Produces(MediaType.APPLICATION_JSON)
@Api("Schema API: Create, Read and Update operations")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SchemaResource {

    private final ValidationSchemaDBCommand schemaProvider;

    @GET
    @ApiOperation("Get all schemas")
    public Response getAllSchema(@DefaultValue("true") @QueryParam("active") boolean active) {
        log.info("Request received to fetch all schemas");
        List<ValidationSchema> schemas = schemaProvider.getAllSchema();
        if (active) {
            schemas = schemas.stream()
                    .filter(ValidationSchema::isActive)
                    .collect(Collectors.toList());
        }

        log.info("Response list of schemas : {}", schemas);
        return Response.ok()
                .entity(schemas)
                .build();
    }

    @GET
    @Path("/{schemaId}")
    @ApiOperation("Get schema for given ID")
    public Response getSchema(@PathParam("schemaId") @Valid @NotBlank final String schemaId) {
        log.info("Request received to fetch schema for uuid : {}", schemaId);

        String uuid = schemaId.trim();
        Optional<ValidationSchema> optionalSchema = schemaProvider.getSchema(uuid);

        if (optionalSchema.isPresent()) {
            log.info("Response object : {}", optionalSchema.get());
            return Response.ok()
                    .entity(optionalSchema.get())
                    .build();
        } else {
            log.warn("No schema found for uuid: {}", schemaId);
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @POST
    @Path("/create")
    @ApiOperation("Create a schema")
    public Response createSchema(@Valid NewSchemaRequest request) {
        log.info("Request received to create schema : {}", request);

        //TODO: Add validator for request
        Optional<ValidationSchema> optionalSchema = schemaProvider.createSchema(ValidationSchemaUtils
                .toSchema(request));
        if (optionalSchema.isPresent()) {
            log.info("Response object : {}", optionalSchema.get());
            return Response.ok()
                    .entity(optionalSchema.get())
                    .build();
        } else {
            log.warn("Failed to generate schema for request: {}", request);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @POST
    @Path("/update/{schemaId}")
    public Response updateSchema(@PathParam("schemaId") String schemaId,
                                 @Valid UpdateSchemaRequest request) {
        log.info("Request received for schema update : id {} request : {}", schemaId, request);

        Optional<ValidationSchema> optionalOldSchema = schemaProvider.getSchema(schemaId);
        if (!optionalOldSchema.isPresent()) {
            log.warn("No schema found for uuid: {}", schemaId);
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }

        ValidationSchema newSchema = ValidationSchemaUtils.updateSchema(optionalOldSchema.get(), request);
        Optional<ValidationSchema> optionalNewSchema = schemaProvider.updateSchema(newSchema);
        if (optionalNewSchema.isPresent()) {
            log.info("Response object : {}", optionalNewSchema.get());
            return Response.ok()
                    .entity(optionalNewSchema.get())
                    .build();
        } else {
            log.warn("Failed to update schema for id {} request: {}", schemaId, request);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

}
