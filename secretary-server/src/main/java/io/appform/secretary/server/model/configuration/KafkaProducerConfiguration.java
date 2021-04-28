package io.appform.secretary.server.model.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import java.util.Properties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaProducerConfiguration {

    @NotBlank
    private String servers;

    @NotBlank
    private String ack;

    @Min(0)
    private int retries;

    @Min(0)
    private int batchSize;

    @Min(0)
    private int lingerMilliseconds;

    @Min(4096)
    private int bufferSize;

    @NotBlank
    private String serializer;

    @NotBlank
    private String deserializer;

    public Properties getProperties() {

        Properties properties = new Properties();

        properties.put("bootstrap.servers", this.servers);
        properties.put("acks", this.ack);
        properties.put("retries", this.retries);
        properties.put("batch.size", this.batchSize);
        properties.put("linger.ms", this.lingerMilliseconds);
        properties.put("buffer.memory", this.bufferSize);
        properties.put("key.serializer", this.serializer);
        properties.put("value.serializer", this.deserializer);

        return properties;
    }
}
