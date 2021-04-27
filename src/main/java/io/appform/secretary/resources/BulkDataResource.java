package io.appform.secretary.resources;

import io.appform.secretary.executor.DataExecutor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotBlank;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Slf4j
@Path("/v1/data")
@Produces(MediaType.APPLICATION_JSON)
//@Api("Data Processing APIs")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class BulkDataResource {

    private final DataExecutor executor;

    //TODO: Fix compatibility issue between Swagger and Dropwizard Forms
    @POST
    @Path("/file/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @ApiOperation("Upload a file for processing")
    @SneakyThrows
    public Response processDataFile(@Valid @NotNull @FormDataParam("file") InputStream fileStream,
                                    @Valid @NotNull @FormDataParam("file") FormDataContentDisposition fileMetaData,
                                    @Valid @NotBlank @FormDataParam("user") String userId,
                                    @Valid @NotBlank @FormDataParam("flow") String workflow) {
        log.info("Request: Upload filename : {} workflowId: {} userId: {}",
                fileMetaData.getName(), workflow, userId);

        //TODO: Convert input to object using validator; perfom sanity check on workflow and file hashsum
        executor.processFile(fileStream, fileMetaData.getFileName(), workflow, userId);

        log.info("Response: Successfully processed file: {}", fileMetaData.getName());
        return Response.ok().build();
    }
}
