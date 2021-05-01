package io.appform.secretary.server.resources;

import io.appform.secretary.model.configuration.SecretaryConfiguration;
import io.appform.secretary.server.views.ConsoleView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("/v1/console")
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
@Api("UI APIs")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UiResource {

    private static final String CONSOLE_TEMPLATE = "console.mustache";

    private final SecretaryConfiguration configuration;

    @GET
    @ApiOperation("Generates HTML response for view")
    public ConsoleView getView() {
        return new ConsoleView(CONSOLE_TEMPLATE,
                configuration.getServiceBaseUrl() + configuration.getFileUploadPath());
    }
}
