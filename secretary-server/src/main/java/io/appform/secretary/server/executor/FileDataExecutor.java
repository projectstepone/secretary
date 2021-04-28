package io.appform.secretary.server.executor;

import com.google.inject.Singleton;
import io.appform.secretary.server.command.FileDataDBCommand;
import io.appform.secretary.server.command.KafkaProducerCommand;
import io.appform.secretary.server.model.DataEntry;
import io.appform.secretary.server.model.FileData;
import io.appform.secretary.server.model.InputFileData;
import io.appform.secretary.server.model.KafkaMessage;
import io.appform.secretary.server.model.state.FileState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    public void processFile(InputFileData data) {
        try {
            FileData savedData = saveEntryInDbAndSendEvent(FileData.builder()
                    .name(data.getFile())
                    .workflow(data.getWorkflow())
                    .user(data.getUser())
                    .state(FileState.ACCEPTED)
                    .hash(data.getHash())
                    .build());

            //TODO: Handle header row
            List<DataEntry> dataEntries = getRows(data.getContent());
            List<DataEntry> validEntries = dataEntries.stream()
                    .map(this::validateRow)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            savedData = updateEntryInDbAndSendEvent(savedData, FileState.PROCESSING);

            List<KafkaMessage> messages = validEntries.stream()
                    .map(entry -> KafkaMessage.builder()
                            .topic(KAFKA_TOPIC_FILEDATA_INGESTION)
                            .key(UUID.randomUUID().toString())
                            .value(entry.getEntryList().toString())
                            .build())
                    .collect(Collectors.toList());
            kafkaProducer.push(messages);

            updateEntryInDbAndSendEvent(savedData, FileState.PROCESSED);
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

    private List<DataEntry> getRows(byte[] data) {
        return Arrays.stream(new String(data, StandardCharsets.UTF_8).split("\n"))
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

    private FileData saveEntryInDbAndSendEvent(FileData data) {
        Optional<FileData> savedData = dbCommand.save(data);

        //TODO: Send event based on error check
        return savedData.get();
    }

    private FileData updateEntryInDbAndSendEvent(FileData data, FileState state) {
        data.setState(state);
        Optional<FileData> savedData = dbCommand.update(data);

        //TODO: Send event based on error check
        return savedData.get();
    }
}
