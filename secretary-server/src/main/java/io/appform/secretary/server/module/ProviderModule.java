package io.appform.secretary.server.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.appform.secretary.model.configuration.SecretaryConfiguration;
import io.appform.secretary.server.AppConfig;
import io.appform.secretary.server.command.FileDataProvider;
import io.appform.secretary.server.command.FileRowDataProvider;
import io.appform.secretary.server.command.FileSchemaProvider;
import io.appform.secretary.server.command.CellSchemaProvider;
import io.appform.secretary.server.command.WorkflowProvider;
import io.appform.secretary.server.command.impl.FileDataDBCommand;
import io.appform.secretary.server.command.impl.FileRowDataDBCommand;
import io.appform.secretary.server.command.impl.FileSchemaDBCommand;
import io.appform.secretary.server.command.impl.CellSchemaDBCommand;
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
        bind(CellSchemaProvider.class).to(CellSchemaDBCommand.class);
        bind(FileSchemaProvider.class).to(FileSchemaDBCommand.class);
    }

    @Singleton
    @Provides
    public SecretaryConfiguration providerSecretaryConfiguration(AppConfig appConfig) {
        return appConfig.getSecretaryConfig();
    }
}
