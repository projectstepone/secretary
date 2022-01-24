package io.appform.secretary.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Stage;
import in.vectorpro.dropwizard.swagger.SwaggerBundle;
import in.vectorpro.dropwizard.swagger.SwaggerBundleConfiguration;
import io.appform.dropwizard.actors.RabbitmqActorBundle;
import io.appform.dropwizard.actors.TtlConfig;
import io.appform.dropwizard.actors.config.RMQConfig;
import io.appform.dropwizard.sharding.DBShardingBundle;
import io.appform.dropwizard.sharding.config.ShardedHibernateFactory;
import io.appform.idman.authbundle.IdmanAuthBundle;
import io.appform.idman.client.http.IdManHttpClientConfig;
import io.appform.secretary.server.exception.GenericExceptionMapper;
import io.appform.secretary.server.module.ClientModule;
import io.appform.secretary.server.module.DBModule;
import io.appform.secretary.server.module.ProviderModule;
import io.appform.secretary.server.utils.MapperUtils;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import lombok.val;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class App extends Application<AppConfig> {

    public static void main(final String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "secretary";
    }

    @Override
    public void initialize(final Bootstrap<AppConfig> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false))
        );

        val mapper = bootstrap.getObjectMapper();
        setMapperProperties(mapper);
        MapperUtils.initialize(mapper);

        val dbBundle = getDBShardingBundle();
        val actorBundle = initRmqBundle();
        bootstrap.addBundle(actorBundle);
        bootstrap.addBundle(dbBundle);
        bootstrap.addBundle(getGuiceBundle(dbBundle, actorBundle));
        bootstrap.addBundle(getSwaggerBundle());
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new ViewBundle<>());
        bootstrap.addBundle(idmanAuthBundle());
        bootstrap.addBundle(new AssetsBundle());
    }

    @Override
    public void run(final AppConfig configuration,
                    final Environment environment) {
        environment.jersey().register(GenericExceptionMapper.class);
    }

    private DBShardingBundle<AppConfig> getDBShardingBundle() {
        return new DBShardingBundle<AppConfig>("io.appform.secretary") {
            @Override
            protected ShardedHibernateFactory getConfig(AppConfig appConfig) {
                return appConfig.getShards();
            }
        };
    }

    private GuiceBundle getGuiceBundle(DBShardingBundle<AppConfig> dbShardingBundle, RabbitmqActorBundle<AppConfig> actorBundle) {
        return GuiceBundle.<AppConfig>builder()
                .enableAutoConfig(getClass().getPackage().getName())
                .modules(new DBModule(dbShardingBundle))
                .modules(new ClientModule(actorBundle))
                .modules(new ProviderModule())
                .build(Stage.PRODUCTION);
    }

    private SwaggerBundle<AppConfig> getSwaggerBundle() {
        return new SwaggerBundle<AppConfig>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(AppConfig config) {
                return config.getSwagger();
            }
        };
    }

    private RabbitmqActorBundle<AppConfig> initRmqBundle() {
        return new RabbitmqActorBundle<AppConfig>() {
            @Override
            protected TtlConfig ttlConfig() {
                return null;
            }

            @Override
            protected RMQConfig getConfig(final AppConfig config) {
                return config.getRmqConfig();
            }
        };
    }

    private IdmanAuthBundle<AppConfig> idmanAuthBundle() {
        return new IdmanAuthBundle<AppConfig>() {
            @Override
            public void initialize(Bootstrap<?> bootstrap) {
                //
            }

            @Override
            public IdManHttpClientConfig clientConfig(AppConfig config) {
                return config.getIdManHttpClientConfig();
            }
        };
    }

    private void setMapperProperties(ObjectMapper mapper) {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
    }

}