package io.appform.secretary.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMessage {

    private String topic;
    private String key;
    //TODO: Change from String to byte array to generalise
    private String value;
}
