package io.appform.secretary.server.exception;

import io.appform.secretary.model.GenericResponse;
import io.appform.secretary.model.exception.SecretaryError;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Slf4j
public class GenericExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException ex) {
        //TODO: Add event for exception
        log.error("Exception occurred: {}", ex.getMessage());

        if (ex instanceof SecretaryError) {
            final SecretaryError error = (SecretaryError) ex;
            return Response
                    .status(error.getResponseCode().getCode())
                    .entity(GenericResponse.builder()
                            .success(false)
                            .error(error.getMessage())
                            .build())
                    .build();
        }

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(GenericResponse.builder()
                        .success(false)
                        .error(ex.getMessage())
                        .build())
                .build();
    }
}
