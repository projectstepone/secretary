package io.appform.secretary.server.resources;

import com.google.inject.Singleton;
import io.appform.idman.authcomponents.security.ServiceUserPrincipal;
import io.appform.secretary.server.command.WorkflowProvider;
import io.appform.secretary.server.command.impl.FileDataDBCommand;
import io.appform.secretary.server.views.ConsoleView;
import io.dropwizard.auth.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("v1/console")
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
@Tag(name = "UI apis")
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UiResource {

    private static final String CONSOLE_TEMPLATE = "console.mustache";

    private final WorkflowProvider dbCommand;
    private final FileDataDBCommand fileDataDBCommand;

    @GET
    @RolesAllowed("SECRETARY_USER")
    @Operation(summary = "Generates html console view")
    public ConsoleView getView(@Auth final ServiceUserPrincipal principal) {
        return new ConsoleView(CONSOLE_TEMPLATE, principal.getServiceUser(), dbCommand.getAll(), fileDataDBCommand.getAll());
    }
}
