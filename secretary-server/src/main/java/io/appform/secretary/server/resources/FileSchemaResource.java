package io.appform.secretary.server.resources;


import com.google.inject.Singleton;
import io.appform.secretary.model.GenericResponse;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.model.schema.file.request.CreateRequest;
import io.appform.secretary.server.command.FileSchemaProvider;
import io.appform.secretary.server.translator.request.FileSchemaTranslator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@Path("v1/schema/file")
@Produces(MediaType.APPLICATION_JSON)
@Api("File Schema APIs")
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileSchemaResource {

    private final FileSchemaProvider fileSchemaProvider;
    private final FileSchemaTranslator fileSchemaTranslator;

    @POST
    @Path("/create")
    @ApiOperation("Create a file schema")
    public Response createFileSchema(@Valid CreateRequest request) {
        log.info("Request: Create file schema : {}", request);

        val schemaRequest = fileSchemaTranslator.translate(request);
        val fileSchema = fileSchemaProvider.save(schemaRequest);
        if (fileSchema.isPresent()) {
            log.info("Response: File schema: {}", fileSchema.get());
            return Response.ok()
                    .entity(GenericResponse.builder()
                            .success(true)
                            .data(fileSchema.get())
                            .build())
                    .build();
        } else {
            throw new SecretaryError("Unable to create file schema: " + request,
                    ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Path("/update")
    @ApiOperation("Updates a file schema")
    public Response updateFileSchema(@Valid CreateRequest request) {
        log.info("Request: update file schema : {}", request);

        val fileSchema = fileSchemaTranslator.translate(request);
        val updatedFileSchema = fileSchemaProvider.update(fileSchema);
        if (updatedFileSchema.isPresent()) {
            log.info("Response: File schema: {}", updatedFileSchema.get());
            return Response.ok()
                    .entity(GenericResponse.builder()
                            .success(true)
                            .data(updatedFileSchema.get())
                            .build())
                    .build();
        } else {
            throw new SecretaryError("Unable to update file schema: " + request,
                    ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("/{workflow}")
    @ApiOperation("Gets a file schema")
    public Response getFileSchema(@PathParam("workflow") String workflow) {
        val fileSchema = fileSchemaProvider.get(workflow);
        if (fileSchema.isPresent()) {
            return Response.ok()
                    .entity(GenericResponse.builder()
                            .success(true)
                            .data(fileSchema.get())
                            .build())
                    .build();
        }
        return Response.ok()
                .entity("Schema not found")
                .build();
    }

}
