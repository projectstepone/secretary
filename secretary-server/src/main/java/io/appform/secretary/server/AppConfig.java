package io.appform.secretary.server;

import in.vectorpro.dropwizard.swagger.SwaggerBundleConfiguration;
import io.appform.dropwizard.actors.actor.ActorConfig;
import io.appform.dropwizard.actors.config.RMQConfig;
import io.appform.dropwizard.sharding.config.ShardedHibernateFactory;
import io.appform.eventingester.client.EventPublisherConfig;
import io.appform.http.client.models.HttpConfiguration;
import io.appform.idman.client.http.IdManHttpClientConfig;
import io.appform.secretary.server.actors.ActorType;
import io.dropwizard.Configuration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig extends Configuration {

    @NotNull
    @Valid
    private ShardedHibernateFactory shards;

    @NotNull
    @Valid
    private SwaggerBundleConfiguration swagger = new SwaggerBundleConfiguration();

    @Valid
    @NotNull
    private RMQConfig rmqConfig;

    @Valid
    @NotNull
    private Map<ActorType, ActorConfig> actorConfigs;

    @Valid
    @NotNull
    private HttpConfiguration statesmanHttpConfiguration;

    @Valid
    @NotNull
    private IdManHttpClientConfig idManHttpClientConfig = new IdManHttpClientConfig();

    @Valid
    @NotNull
    private EventPublisherConfig eventPublisherConfig;
}
