package io.appform.secretary.server.resources;


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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@Path("/v1/schema/file")
@Produces(MediaType.APPLICATION_JSON)
@Api("File Schema APIs")
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

}
