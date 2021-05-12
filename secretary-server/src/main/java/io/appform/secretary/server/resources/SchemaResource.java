package io.appform.secretary.server.resources;

import io.appform.secretary.model.GenericResponse;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.model.schema.cell.request.CreateSchemaRequest;
import io.appform.secretary.model.schema.cell.request.UpdateSchemaRequest;
import io.appform.secretary.server.command.FileSchemaProvider;
import io.appform.secretary.server.command.ValidationSchemaProvider;
import io.appform.secretary.server.translator.request.FileSchemaRequestTranslator;
import io.appform.secretary.server.translator.request.SchemaRequestTranslator;
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
@Path("/v1/cellSchema")
@Produces(MediaType.APPLICATION_JSON)
@Api("CellSchema APIs")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SchemaResource {

    private final ValidationSchemaProvider schemaProvider;
    private final FileSchemaProvider fileSchemaProvider;
    private final FileSchemaRequestTranslator fileSchemaTranslator;

    @GET
    @ApiOperation("Get all schemas")
    public Response getAllSchema(@QueryParam("active") boolean active) {
        log.info("Request received to fetch all cellSchemas");
        List<CellSchema> cellSchemas = schemaProvider.getAll();
        if (active) {
            cellSchemas = cellSchemas.stream()
                    .filter(CellSchema::isActive)
                    .collect(Collectors.toList());
        }

        log.info("Response list of cellSchemas : {}", cellSchemas);
        return Response.ok()
                .entity(cellSchemas)
                .build();
    }

    @GET
    @Path("/{schemaId}")
    @ApiOperation("Get cellSchema for given ID")
    public Response getSchema(@PathParam("schemaId") @Valid @NotBlank final String schemaId) {
        log.info("Request received to fetch cellSchema for uuid : {}", schemaId);

        val uuid = schemaId.trim();
        val optionalSchema = schemaProvider.get(uuid);

        if (optionalSchema.isPresent()) {
            log.info("Response: CellSchema : {}", optionalSchema.get());
            return Response.ok()
                    .entity(GenericResponse.builder()
                            .success(true)
                            .data(optionalSchema.get())
                            .build())
                    .build();
        } else {
            throw new SecretaryError("Unable to find cellSchema: " + uuid,
                    ResponseCode.BAD_REQUEST);
        }
    }

    @POST
    @Path("/create")
    @ApiOperation("Create a cellSchema")
    public Response createSchema(@Valid CreateSchemaRequest request) {
        log.info("Request received to create cellSchema : {}", request);

        //TODO: Filter instance of abstract class
        //TODO: Add validator for request
        Optional<CellSchema> optionalSchema = schemaProvider.save(SchemaRequestTranslator.createSchema(request));
        if (optionalSchema.isPresent()) {
            log.info("Response object : {}", optionalSchema.get());
            return Response.ok()
                    .entity(optionalSchema.get())
                    .build();
        } else {
            throw new SecretaryError("Unable to create cellSchema: " + request,
                    ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Path("/update/{schemaId}")
    @ApiOperation("Update cellSchema")
    public Response updateSchema(@PathParam("schemaId") String schemaId,
                                 @Valid UpdateSchemaRequest request) {
        log.info("Request received for cellSchema update : id {} request : {}", schemaId, request);

        val schema = schemaProvider.get(schemaId);
        if (!schema.isPresent()) {
            throw new SecretaryError("Unable to find cellSchema: " + schemaId,
                    ResponseCode.BAD_REQUEST);
        }

        val newSchema = schemaProvider.update(SchemaRequestTranslator.updateSchema(request, schema.get()));
        if (newSchema.isPresent()) {
            log.info("Response: Updated cellSchema : {}", newSchema.get());
            return Response.ok()
                    .entity(GenericResponse.builder()
                            .success(true)
                            .data(newSchema.get())
                            .build())
                    .build();
        } else {
            throw new SecretaryError("Unable to update cellSchema: " + request,
                    ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/file/create")
    @ApiOperation("Create a file cellSchema")
    public Response createFileSchema(@Valid io.appform.secretary.model.schema.file.request.CreateSchemaRequest request) {
        log.info("Request received to create cellSchema : {}", request);

        val schemaRequest = fileSchemaTranslator.getFileSchema(request);
        val fileSchema = fileSchemaProvider.save(schemaRequest);
        if (fileSchema.isPresent()) {
            log.info("Response: File cellSchema: {}", fileSchema.get());
            return Response.ok()
                    .entity(GenericResponse.builder()
                            .success(true)
                            .data(fileSchema.get())
                            .build())
                    .build();
        } else {
            throw new SecretaryError("Unable to create file cellSchema: " + request,
                    ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

}
