package io.appform.secretary.server;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import io.appform.dropwizard.actors.RabbitmqActorBundle;
import io.appform.dropwizard.actors.actor.ActorConfig;
import io.appform.dropwizard.actors.config.RMQConfig;
import io.appform.dropwizard.actors.retry.config.CountLimitedExponentialWaitRetryConfig;
import io.appform.dropwizard.sharding.DBShardingBundle;
import io.appform.dropwizard.sharding.config.ShardedHibernateFactory;
import io.appform.eventingester.client.EventPublisherConfig;
import io.appform.http.client.models.HttpConfiguration;
import io.appform.secretary.server.actors.ActorType;
import io.appform.secretary.server.module.ClientModule;
import io.appform.secretary.server.module.DBModule;
import io.appform.secretary.server.module.ProviderModule;
import io.appform.secretary.server.utils.MapperUtils;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.AdminEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith({DropwizardExtension.class, MockServerExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestBase {

    private DBShardingBundle<AppConfig> dbShardingBundle;
    protected WireMockServer wireMockServer;

    @BeforeAll
    public void setup(RMQConfig rmqConfig, ShardedHibernateFactory hibernateFactory, WireMockServer wireMockServer) {
        log.info("rmqConfig: {}, hibernateFactory: {}", rmqConfig, hibernateFactory);
        this.wireMockServer = wireMockServer;
        val appConfig = new AppConfig();
        val metricRegistry = new MetricRegistry();
        val environment = mock(Environment.class);
        val healthChecks = mock(HealthCheckRegistry.class);
        val jerseyEnvironment = mock(JerseyEnvironment.class);
        val lifecycleEnvironment = new LifecycleEnvironment(metricRegistry);
        val admin = mock(AdminEnvironment.class);
        val bootstrap = mock(Bootstrap.class);
        MapperUtils.initialize(new ObjectMapper());

        appConfig.setStatesmanHttpConfiguration(statesmanHttpClientConfig());
        appConfig.setEventPublisherConfig(getEventPublisherConfig());
        appConfig.setActorConfigs(actorConfigs());
        appConfig.setRmqConfig(rmqConfig);
        appConfig.setShards(hibernateFactory);

        when(jerseyEnvironment.getResourceConfig()).thenReturn(new DropwizardResourceConfig());
        when(environment.jersey()).thenReturn(jerseyEnvironment);
        when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
        when(environment.healthChecks()).thenReturn(healthChecks);
        when(environment.admin()).thenReturn(admin);
        when(environment.metrics()).thenReturn(new MetricRegistry());
        when(bootstrap.getHealthCheckRegistry()).thenReturn(Mockito.mock(HealthCheckRegistry.class));

        val rmqBundle = new RabbitmqActorBundle<AppConfig>() {
            @Override
            protected RMQConfig getConfig(final AppConfig config) {
                return config.getRmqConfig();
            }
        };
        dbShardingBundle = new DBShardingBundle<AppConfig>("io.appform.secretary") {
            @Override
            protected ShardedHibernateFactory getConfig(AppConfig appConfig) {
                return appConfig.getShards();
            }
        };

        rmqBundle.initialize(bootstrap);
        rmqBundle.run(appConfig, environment);
        dbShardingBundle.initBundles(bootstrap);
        dbShardingBundle.initialize(bootstrap);
        dbShardingBundle.runBundles(appConfig, environment);
        dbShardingBundle.run(appConfig, environment);

        val clientModule = new ClientModule(rmqBundle);
        val dbModule = new DBModule(dbShardingBundle);
        val providerModule = new ProviderModule();

        val injector = Guice.createInjector(clientModule, dbModule, providerModule);
        injector.injectMembers(metricRegistry);
        injector.injectMembers(environment);
        injector.injectMembers(healthChecks);
        injector.injectMembers(jerseyEnvironment);
        injector.injectMembers(lifecycleEnvironment);
        injector.injectMembers(bootstrap);
        injector.injectMembers(this);
    }

    @AfterAll
    public void tearDown() {
        dbShardingBundle.getSessionFactories().forEach(this::truncateTables);
    }

    private HttpConfiguration statesmanHttpClientConfig() {
        return HttpConfiguration.builder()
                .clientName("statesman")
                .connections(20)
                .connectTimeoutMs(3000)
                .idleTimeOutSeconds(3000)
                .opTimeoutMs(3000)
                .host("")
                .port(3455)
                .secure(false)
                .build();
    }

    private EventPublisherConfig getEventPublisherConfig() {
        val config = new EventPublisherConfig();
        config.setServer("http://localhost:3030");
        config.setQueuePath("/tmp/secretary");
        return config;
    }

    private Map<ActorType, ActorConfig> actorConfigs() {
        final Map<ActorType, ActorConfig> configs = new HashMap<>();
        val config = new ActorConfig();
        val retryerConfig = CountLimitedExponentialWaitRetryConfig.builder()
                .maxAttempts(6)
                .multipier(100)
                .maxTimeBetweenRetries(Duration.seconds(20))
                .build();
        config.setPrefetchCount(2);
        config.setConcurrency(2);
        config.setPrefix("secretary.actor");
        config.setPrefix("secretary.actor");
        config.setExchange("secretary.rows");
        config.setRetryConfig(retryerConfig);
        configs.put(ActorType.FILE_ROW_PROCESSOR, config);
        return configs;
    }

    private void truncateTables(SessionFactory sessionFactory) {
        log.info("Truncating tables");
        Session session = sessionFactory.openSession();
        val query = session.createNativeQuery("show tables");
        Set<String> tables = Sets.newHashSet();
        List rows = query.getResultList();
        for (Object row : rows) {
           log.info("row: {}", row.toString());
            tables.add(row.toString());
        }

        List<String> queries = Lists.newArrayList();
        queries.add("SET foreign_key_checks = 0;");
        tables.forEach(table -> queries.add(String.format("delete from %s where true;", table)));
        queries.add("SET foreign_key_checks = 1;");

        Transaction transaction = session.beginTransaction();
        queries.forEach(q -> {
            session.createNativeQuery(q).executeUpdate();
        });
        transaction.commit();

        session.close();
        log.info("Truncated tables");
    }
}
