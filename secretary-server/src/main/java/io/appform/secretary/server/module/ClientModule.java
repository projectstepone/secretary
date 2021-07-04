package io.appform.secretary.server.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.appform.dropwizard.actors.ConnectionRegistry;
import io.appform.dropwizard.actors.RabbitmqActorBundle;
import io.appform.dropwizard.actors.connectivity.RMQConnection;
import io.appform.http.client.models.HttpConfiguration;
import io.appform.secretary.server.AppConfig;

public class ClientModule extends AbstractModule {

    private final RabbitmqActorBundle<AppConfig> actorBundle;

    public ClientModule(final RabbitmqActorBundle<AppConfig> actorBundle) {
        this.actorBundle = actorBundle;
    }

    @Provides
    @Singleton
    public RMQConnection getRMQConnection() {
        return actorBundle.getConnection();
    }


    @Provides
    @Singleton
    public ConnectionRegistry getConnectionRegistry() {
        return actorBundle.getConnectionRegistry();
    }

    @Provides
    @Singleton
    @Named("statesmanHttpConfiguration")
    public HttpConfiguration getStatesmanHttpConfiguration(AppConfig appConfig) {
        return appConfig.getStatesmanHttpConfiguration();
    }
}
