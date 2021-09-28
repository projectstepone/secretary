package io.appform.secretary.server.views;

import io.appform.idman.model.IdmanUser;
import io.appform.secretary.model.FileData;
import io.appform.secretary.model.Workflow;
import io.dropwizard.views.View;
import lombok.Getter;

import java.util.List;

public class ConsoleView extends View {

    private static final String TEMPLATE_PATH = "/views/";

    @Getter
    private IdmanUser idmanUser;
    @Getter
    private List<Workflow> workflows;
    @Getter
    private List<FileData> fileDatas;

    public ConsoleView(String templateName, IdmanUser idmanUser, List<Workflow> workflows, List<FileData> fileDatas) {
        super(TEMPLATE_PATH + templateName);
        this.idmanUser = idmanUser;
        this.workflows = workflows;
        this.fileDatas = fileDatas;
    }
}
