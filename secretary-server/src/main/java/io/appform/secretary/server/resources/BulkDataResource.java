package io.appform.secretary.server.resources;

import io.appform.secretary.server.executor.DataExecutor;
import io.appform.secretary.server.model.InputFileData;
import io.appform.secretary.server.utils.CommonUtils;
import io.appform.secretary.server.validator.FileInputValidator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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
import java.util.Optional;

@Slf4j
@Path("/v1/data")
@Produces(MediaType.APPLICATION_JSON)
//@Api("Data Processing APIs")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class BulkDataResource {

    private final DataExecutor executor;
    private final FileInputValidator validator;

    //TODO: Fix compatibility issue between Swagger and Dropwizard Forms
    @POST
    @Path("/file/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @ApiOperation("Upload a file for processing")
    @SneakyThrows
    public Response processDataFile(@Valid @NotNull @FormDataParam("file") InputStream fileStream,
                                    @Valid @NotNull @FormDataParam("file") FormDataContentDisposition fileMetaData,
                                    @Valid @NotBlank @FormDataParam("user") String user,
                                    @Valid @NotBlank @FormDataParam("flow") String workflow) {
        log.info("Request: Upload filename : {} workflow: {} user: {}",
                fileMetaData.getFileName(), workflow, user);

        InputFileData data = InputFileData.builder()
                .file(fileMetaData.getFileName())
                .content(IOUtils.toByteArray(fileStream))
                .user(user)
                .workflow(workflow)
                .build();
        data.setHash(CommonUtils.getHash(data.getContent()));
        log.info("Converted request to data object");

        Optional<String> validationFailure = validator.isValid(data);
        if (validationFailure.isPresent()) {
            log.error("Bad request: {}", validationFailure.get());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(validationFailure.get())
                    .build();
        }

        executor.processFile(data);

        log.info("Response: Successfully processed file: {}", fileMetaData.getName());
        return Response.ok().build();
    }
}
