package io.appform.secretary.module;

import com.google.inject.AbstractModule;
import io.appform.secretary.command.FileDataDBCommand;
import io.appform.secretary.command.FileDataProvider;
import io.appform.secretary.command.WorkflowDBCommand;
import io.appform.secretary.command.WorkflowProvider;
import io.appform.secretary.executor.DataExecutor;
import io.appform.secretary.executor.FileDataExecutor;

public class ProviderModule extends AbstractModule {

    @Override
    public void configure() {
        bind(WorkflowProvider.class).to(WorkflowDBCommand.class);
        bind(DataExecutor.class).to(FileDataExecutor.class);
        bind(FileDataProvider.class).to(FileDataDBCommand.class);
    }
}
