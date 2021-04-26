package io.appform.secretary.executor;

import com.google.inject.Singleton;
import io.appform.secretary.command.FileDataDBCommand;
import io.appform.secretary.command.KafkaProducerCommand;
import io.appform.secretary.model.DataEntry;
import io.appform.secretary.model.FileData;
import io.appform.secretary.model.KafkaMessage;
import io.appform.secretary.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileDataExecutor implements DataExecutor {

    private static final String KAFKA_TOPIC_FILEDATA_INGESTION = "secretary.filedata.random";
    private final KafkaProducerCommand kafkaProducer;
    private final FileDataDBCommand dbCommand;

    @Override
    public void processFile(InputStream dataStream, String filename, String workflow, String userId) {
        try {
            byte[] data = IOUtils.toByteArray(dataStream);
            FileData fileData = FileData.builder()
                    .name(filename)
                    .workflow(workflow)
                    .user(userId)
                    .processed(false)
                    .hash(CommonUtils.getHash(data))
                    .build();
            dbCommand.save(fileData);


            //TODO: Add entry in DB for file with state as ACCEPTED and send event
            //TODO: Send event about file acceptance

            List<DataEntry> dataEntries = getRows(dataStream);

            List<DataEntry> validEntries = dataEntries.stream()
                    .map(this::validateRow)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            //TODO: Update file state in DB to PARSED and send event

            List<KafkaMessage> messages = validEntries.stream()
                    .map(entry -> KafkaMessage.builder()
                            .topic(KAFKA_TOPIC_FILEDATA_INGESTION)
                            .key(UUID.randomUUID().toString())
                            .value(entry.getEntryList().toString())
                            .build())
                    .collect(Collectors.toList());
            kafkaProducer.push(messages);

            //TODO: Update file state in DB to CONSUMED and send event
        } catch (Exception ex) {
            log.warn("Hit exception : {}", ex.getMessage());
        }
    }

    private DataEntry validateRow(DataEntry entry) {
        if (Objects.isNull(entry)) {
            return null;
        }

        boolean emptyValues = entry.getEntryList().stream()
                .anyMatch(String::isEmpty);
        if (emptyValues) {
            return null;
        }

        //TODO: Add schema validation
        return entry;

    }

    private List<DataEntry> getRows(InputStream dataStream) {
        return new BufferedReader(new InputStreamReader(dataStream))
                .lines()
                .map(entry -> DataEntry.builder()
                        .entryList(getTokens(entry))
                        .build())
                .collect(Collectors.toList());
    }

    private List<String> getTokens(String input) {
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .map(entry -> entry.replace(" ", "_"))
                .collect(Collectors.toList());
    }

}
