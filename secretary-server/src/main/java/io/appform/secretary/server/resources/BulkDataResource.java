package io.appform.secretary.server.resources;

import io.appform.secretary.model.FileData;
import io.appform.secretary.model.GenericResponse;
import io.appform.secretary.model.RawDataEntry;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.server.command.FileDataProvider;
import io.appform.secretary.server.command.FileRowDataProvider;
import io.appform.secretary.server.executor.DataExecutor;
import io.appform.secretary.server.internal.model.InputFileData;
import io.appform.secretary.server.utils.CommonUtils;
import io.appform.secretary.server.validator.FileInputValidator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.apache.commons.io.IOUtils;
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
import java.util.Optional;

@Slf4j
@Path("/v1/data")
@Produces(MediaType.APPLICATION_JSON)
//@Api("Data Processing APIs")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class BulkDataResource {

    private final DataExecutor executor;
    private final FileInputValidator validator;
    private final FileDataProvider fileDataProvider;
    private final FileRowDataProvider rowDataProvider;

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

        val data = InputFileData.builder()
                .file(fileMetaData.getFileName())
                .content(IOUtils.toByteArray(fileStream))
                .user(user)
                .workflow(workflow)
                .build();
        data.setHash(CommonUtils.getHash(data.getContent()));
        log.info("Converted request to data object");

        validator.validate(data);
        executor.processFile(data);

        //TODO: Pass UUID for file in response
        log.info("Response: Successfully processed file: {}", fileMetaData.getName());
        return Response.ok().build();
    }

    @GET
    @Path("/status/{fileId}")
    public Response getFileStatus(@Valid @NotBlank @PathParam("fileId") String fileId) {
        log.info("Request: Get status for fileId : {}", fileId);

        Optional<FileData> file = fileDataProvider.get(fileId);
        if (!file.isPresent()) {
            throw new SecretaryError("Invalid file ID: " + fileId, ResponseCode.BAD_REQUEST);
        }

        var output = "";
        if (file.get().getState().isProcessed()) {
            output = "File processing is complete";
        } else {
            List<RawDataEntry> data = rowDataProvider.getByFileId(fileId);
            output = String.format("File: %s; Processed: %s/%s",
                    file.get().getName(), data.size(), file.get().getCount());
        }

        log.info("Output: {}", output);
        return Response.ok()
                .entity(GenericResponse.builder()
                        .success(true)
                        .data(output)
                        .build())
                .build();
    }
}
