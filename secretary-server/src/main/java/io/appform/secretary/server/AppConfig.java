package io.appform.secretary.server;


import io.appform.dropwizard.sharding.config.ShardedHibernateFactory;
import io.appform.secretary.server.model.configuration.KafkaProducerConfiguration;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

    @NotNull
    @Valid
    private KafkaProducerConfiguration producer;
}
