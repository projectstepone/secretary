package io.appform.secretary.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.appform.dropwizard.actors.config.Broker;
import io.appform.dropwizard.actors.config.RMQConfig;
import io.appform.dropwizard.sharding.config.ShardedHibernateFactory;
import io.dropwizard.db.DataSourceFactory;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@Slf4j
public class DropwizardExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, ParameterResolver {

    private static GenericContainer shard1 = new MariaDBContainer(DockerImageName.parse("mariadb:10.0.38"))
            .withInitScript("001-base-tables.sql")
            .withDatabaseName("secretary_00")
            .withUsername("db_user")
            .withPassword("db_password")
            .withExposedPorts(3306);

    private static GenericContainer shard2 = new MariaDBContainer(DockerImageName.parse("mariadb:10.0.38"))
            .withInitScript("001-base-tables.sql")
            .withDatabaseName("secretary_01")
            .withUsername("db_user")
            .withPassword("db_password")
            .withExposedPorts(3306);

    private static GenericContainer rmq = new GenericContainer(DockerImageName.parse("rabbitmq:3.8-management"))
            .withExposedPorts(5672, 15672);

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (!shard1.isRunning()) {
            shard1.start();
        }
        if (!shard2.isRunning()) {
            shard2.start();
        }
        if (!rmq.isRunning()) {
            rmq.start();
        }
        val shardedHibernateFactory = ShardedHibernateFactory.builder()
                .shard(createDataSourceFactory(String.format("jdbc:mariadb://%s:%d/secretary_00?createDatabaseIfNotExist=true&autoReconnect=true", shard1.getHost(), shard1.getMappedPort(3306))))
                .shard(createDataSourceFactory(String.format("jdbc:mariadb://%s:%d/secretary_01?createDatabaseIfNotExist=true&autoReconnect=true", shard2.getHost(), shard2.getMappedPort(3306))))
                .build();
        val brokers = Lists.newArrayList(Broker.builder()
                .host(rmq.getHost())
                .port(rmq.getMappedPort(5672))
                .build());
        val rmqConfig = RMQConfig.builder()
                .brokers(brokers)
                .userName("guest")
                .password("guest")
                .threadPoolSize(20)
                .secure(false)
                .build();
        val namespace = ExtensionContext.Namespace.create(DropwizardExtension.class);
        val store = extensionContext.getRoot().getStore(namespace);

        store.getOrComputeIfAbsent(RMQConfig.class, key -> rmqConfig);
        store.getOrComputeIfAbsent(ShardedHibernateFactory.class, key -> shardedHibernateFactory);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        val type = parameterContext.getParameter().getType();
        return type == RMQConfig.class || type == ShardedHibernateFactory.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        val namespace = ExtensionContext.Namespace.create(DropwizardExtension.class);
        val store = extensionContext.getRoot().getStore(namespace);
        return store.get(parameterContext.getParameter().getType());
    }

    private DataSourceFactory createDataSourceFactory(final String url) {
        final Map<String, String> properties = Maps.newHashMap();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        final DataSourceFactory shard = new DataSourceFactory();
        shard.setInitialSize(1);
        shard.setMinSize(1);
        shard.setMaxSize(1);
        shard.setDriverClass("org.mariadb.jdbc.Driver");
        shard.setInitialSize(1);
        shard.setMinSize(1);
        shard.setMaxSize(1);
        shard.setUrl(url);
        shard.setValidationQuery("select 1");
        shard.setProperties(properties);
        shard.setUser("db_user");
        shard.setPassword("db_password");
        return shard;
    }
}
