package io.appform.secretary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Stage;
import io.appform.dropwizard.sharding.DBShardingBundle;
import io.appform.dropwizard.sharding.config.ShardedHibernateFactory;
import io.appform.secretary.exception.GenericExceptionMapper;
import io.appform.secretary.module.DBModule;
import io.appform.secretary.utils.MapperUtils;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
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

        ObjectMapper mapper = bootstrap.getObjectMapper();
        setMapperProperties(mapper);
        MapperUtils.initialize(mapper);

        final DBShardingBundle<AppConfig> dbBundle = getDBShardingBundle();

        bootstrap.addBundle(dbBundle);
        bootstrap.addBundle(getGuiceBundle(dbBundle));
        bootstrap.addBundle(getSwaggerBundle());
        bootstrap.addBundle(new MultiPartBundle());
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

    private GuiceBundle<AppConfig> getGuiceBundle(DBShardingBundle<AppConfig> dbShardingBundle) {
        return GuiceBundle.<AppConfig>builder()
                .enableAutoConfig(getClass().getPackage().getName())
                .modules(new DBModule(dbShardingBundle))
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

    private void setMapperProperties(ObjectMapper mapper) {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
    }

}