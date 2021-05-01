package io.appform.secretary.server.views;

import io.dropwizard.views.View;
import lombok.Getter;

public class ConsoleView extends View {

    private static final String TEMPLATE_PATH = "/views/";

    @Getter
    private String postUrl;

    public ConsoleView(String templateName, String url) {
        super(TEMPLATE_PATH + templateName);
        this.postUrl = url;
    }
}
