package io.appform.secretary.server.exception;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Slf4j
public class GenericExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException ex) {
        log.error("exception_occurred:", ex);

        //TODO: Create service specific exceptions to encapsulate all exceptions
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .build();
    }
}
