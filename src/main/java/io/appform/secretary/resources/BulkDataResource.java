package io.appform.secretary.resources;

import io.appform.secretary.command.FileDataDBCommand;
import io.appform.secretary.executor.FileDataExecutor;
import io.appform.secretary.model.FileData;
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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Path("/v1/data")
@Produces(MediaType.APPLICATION_JSON)
//@Api("Data Processing APIs")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class BulkDataResource {

    private final FileDataExecutor executor;
    private final FileDataDBCommand dbCommand;

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
        log.info("Received upload request: Filename : {} Workflow: {} UserId: {}",
                fileMetaData.getName(), workflow, userId);

        //TODO: Convert input to object using validator; perfom sanity check on workflow and file hashsum
        executor.processFile(fileStream, fileMetaData.getFileName(), workflow, userId);

        return Response.ok().build();
    }

    @GET
    @Path("file/{userId}")
    public Response getFilesByUser(@Valid @NotBlank @PathParam("userId") String userId) {
        log.info("Received request to list files uploaded by user: {}", userId);

        List<FileData> data = dbCommand.getByUser(userId);

        log.info("List of files uploaded by user {}: {}", userId, data);
        return Response.ok().entity(data).build();
    }
}
