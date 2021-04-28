package io.appform.secretary.server.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.appform.secretary.server.AppConfig;
import io.appform.secretary.server.model.configuration.KafkaProducerConfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;

public class ClientModule extends AbstractModule {

    @Singleton
    @Provides
    public KafkaProducer<String, String> providerKafkaProducer(AppConfig appConfig) {
        KafkaProducerConfiguration configuration = appConfig.getProducer();
        return new KafkaProducer<>(configuration.getProperties());
    }
}