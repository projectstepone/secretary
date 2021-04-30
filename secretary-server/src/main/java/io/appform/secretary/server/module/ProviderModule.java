package io.appform.secretary.server.module;

import com.google.inject.AbstractModule;
import io.appform.secretary.server.command.FileDataProvider;
import io.appform.secretary.server.command.FileRowDataProvider;
import io.appform.secretary.server.command.WorkflowProvider;
import io.appform.secretary.server.command.impl.FileDataDBCommand;
import io.appform.secretary.server.command.impl.FileRowDataDBCommand;
import io.appform.secretary.server.command.impl.WorkflowDBCommand;
import io.appform.secretary.server.executor.DataExecutor;
import io.appform.secretary.server.executor.FileDataExecutor;

public class ProviderModule extends AbstractModule {

    @Override
    public void configure() {
        bind(WorkflowProvider.class).to(WorkflowDBCommand.class);
        bind(DataExecutor.class).to(FileDataExecutor.class);
        bind(FileDataProvider.class).to(FileDataDBCommand.class);
        bind(FileRowDataProvider.class).to(FileRowDataDBCommand.class);
    }
}
