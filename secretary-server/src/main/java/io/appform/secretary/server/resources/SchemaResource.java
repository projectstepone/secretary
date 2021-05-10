package io.appform.secretary.server.resources;

import io.appform.secretary.model.GenericResponse;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.server.command.ValidationSchemaProvider;
import io.appform.secretary.server.internal.model.Schema;
import io.appform.secretary.model.validationschema.NewSchemaRequest;
import io.appform.secretary.model.validationschema.UpdateSchemaRequest;
import io.appform.secretary.server.translator.SchemaRequest;
import io.appform.secretary.server.translator.SchemaTranslator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
@Api("Schema APIs")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SchemaResource {

    private final ValidationSchemaProvider schemaProvider;

    @GET
    @ApiOperation("Get all schemas")
    public Response getAllSchema(@DefaultValue("true") @QueryParam("active") boolean active) {
        log.info("Request received to fetch all schemas");
        List<Schema> schemas = schemaProvider.getAll();
        if (active) {
            schemas = schemas.stream()
                    .filter(Schema::isActive)
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

        val uuid = schemaId.trim();
        val optionalSchema = schemaProvider.get(uuid);

        if (optionalSchema.isPresent()) {
            log.info("Response: Schema : {}", optionalSchema.get());
            return Response.ok()
                    .entity(GenericResponse.builder()
                            .success(true)
                            .data(optionalSchema.get())
                            .build())
                    .build();
        } else {
            throw new SecretaryError("Unable to find schema: " + uuid,
                    ResponseCode.BAD_REQUEST);
        }
    }

    @POST
    @Path("/create")
    @ApiOperation("Create a schema")
    public Response createSchema(@Valid NewSchemaRequest request) {
        log.info("Request received to save schema : {}", request);

        //TODO: Filter instance of abstract class
        //TODO: Add validator for request
        Optional<Schema> optionalSchema = schemaProvider.save(SchemaRequest.createSchema(request));
        if (optionalSchema.isPresent()) {
            log.info("Response object : {}", optionalSchema.get());
            return Response.ok()
                    .entity(optionalSchema.get())
                    .build();
        } else {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
//
//    @POST
//    @Path("/update/{schemaId}")
//    public Response updateSchema(@PathParam("schemaId") String schemaId,
//                                 @Valid UpdateSchemaRequest request) {
//        log.info("Request received for schema update : id {} request : {}", schemaId, request);
//
//        Optional<Schema> optionalOldSchema = schemaProvider.get(schemaId);
//        if (!optionalOldSchema.isPresent()) {
//            log.warn("No schema found for uuid: {}", schemaId);
//            return Response
//                    .status(Response.Status.BAD_REQUEST)
//                    .build();
//        }
//
//        Schema newSchema = SchemaTranslator.update(optionalOldSchema.get(), request);
//        Optional<Schema> optionalNewSchema = schemaProvider.update(newSchema);
//        if (optionalNewSchema.isPresent()) {
//            log.info("Response object : {}", optionalNewSchema.get());
//            return Response.ok()
//                    .entity(optionalNewSchema.get())
//                    .build();
//        } else {
//            log.warn("Failed to update schema for id {} request: {}", schemaId, request);
//            return Response
//                    .status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .build();
//        }
//    }

}
