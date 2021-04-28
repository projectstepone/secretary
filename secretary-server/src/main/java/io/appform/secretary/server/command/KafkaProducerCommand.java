package io.appform.secretary.server.command;

import io.appform.secretary.server.model.KafkaMessage;
import io.dropwizard.lifecycle.Managed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class KafkaProducerCommand implements Managed {

    //TODO: Generalize value type for producer
    private final KafkaProducer<String, String> producer;

    private void pushMessage(KafkaMessage message) {
        producer.send(new ProducerRecord<>(
                message.getTopic(),
                message.getKey(),
                message.getValue())
        );
    }

    public void push(List<KafkaMessage> messages) {
        messages.forEach(message -> {
            try {
                pushMessage(message);
            } catch (Exception ex) {
                log.warn("Failed to send message {}", message);
            }
        });
    }

    @Override
    public void start() {}

    @Override
    public void stop() {
        producer.close();
    }
}
