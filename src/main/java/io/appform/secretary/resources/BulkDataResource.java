package io.appform.secretary.resources;

import io.appform.secretary.executor.FileDataExecutor;
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
import javax.ws.rs.FormParam;
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

    private final FileDataExecutor executor;

    //TODO: Fix compatibility issue between Swagger and Dropwizard Forms
    @POST
    @Path("/file/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @ApiOperation("Upload a file for processing")
    @SneakyThrows
    public Response processDataFile(@Valid @NotNull @FormDataParam("file") InputStream fileStream,
                                    @Valid @NotNull @FormDataParam("file") FormDataContentDisposition fileMetaData,
                                    @Valid @NotBlank @FormDataParam("flow") String workflow) {
        log.info("Received upload request: Filename : {} Workflow: {}",
                fileMetaData.getName(), workflow);

        //TODO: Convert workflow from string to an enum as part of sanity check
        executor.processFile(fileStream, workflow);

        return Response.ok().build();
    }
}
