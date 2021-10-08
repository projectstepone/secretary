package io.appform.secretary.server.utils;

import io.appform.eventingester.models.Event;
import io.appform.secretary.model.FileData;
import io.appform.secretary.server.events.FileAcceptedEvent;
import io.appform.secretary.server.events.SecretaryEvent;
import lombok.experimental.UtilityClass;

import java.util.Date;
import java.util.UUID;

@UtilityClass
public class EventUtils {

    private static final String APP = "secretary";
    private static final String TOPIC = "secretary-reporting";

    public static Event fileAcceptedEvent(FileData data, int validEntryCounts) {
        return Event.builder()
                .id(UUID.randomUUID().toString())
                .eventData(FileAcceptedEvent.builder()
                        .workflow(data.getWorkflow())
                        .rowCounts(validEntryCounts)
                        .build())
                .groupingKey(UUID.randomUUID().toString())
                .eventType(SecretaryEvent.FILE_ACCEPTED.name())
                .app(APP)
                .eventSchemaVersion("v1")
                .partitionKey(UUID.randomUUID().toString())
                .time(new Date())
                .topic(TOPIC)
                .build();
    }
}
