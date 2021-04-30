package io.appform.secretary.server.executor;

import com.google.inject.Singleton;
import io.appform.secretary.model.FileData;
import io.appform.secretary.model.RawDataEntry;
import io.appform.secretary.model.state.FileState;
import io.appform.secretary.server.command.FileRowDataProvider;
import io.appform.secretary.server.command.KafkaProducerCommand;
import io.appform.secretary.server.command.impl.FileDataDBCommand;
import io.appform.secretary.server.internal.model.InputFileData;
import io.appform.secretary.server.internal.model.KafkaMessage;
import io.appform.secretary.server.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileDataExecutor implements DataExecutor {

    private static final String ENTRY_SEPARATOR = ",";
    private static final String LINE_SEPARATOR = "\n";

    private static final String KAFKA_TOPIC_FILEDATA_INGESTION = "secretary.filedata.random";
    private final KafkaProducerCommand kafkaProducer;
    private final FileDataDBCommand dbCommand;
    private final FileRowDataProvider rowDataProvider;

    @Override
    public void processFile(InputFileData data) {
        try {
            var savedData = saveEntryInDbAndSendEvent(FileData.builder()
                    .name(data.getFile())
                    .workflow(data.getWorkflow())
                    .user(data.getUser())
                    .state(FileState.ACCEPTED)
                    .hash(data.getHash())
                    .build());

            val dataEntries = getRows(savedData.getUuid(), data.getContent());
            val validEntries = dataEntries.stream()
                    .map(this::validateRow)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            savedData.setCount(validEntries.size());
            savedData = updateEntryInDbAndSendEvent(savedData, FileState.PROCESSING);

            val messages = validEntries.stream()
                    .filter(entry -> {
                        Optional<RawDataEntry> newData = rowDataProvider.save(entry);
                        return newData.isPresent();
                    })
                    .map(entry -> KafkaMessage.builder()
                            .topic(KAFKA_TOPIC_FILEDATA_INGESTION)
                            .key(UUID.randomUUID().toString())
                            .value(entry.getData().toString())
                            .build())
                    .collect(Collectors.toList());
            kafkaProducer.push(messages);

            val entries = rowDataProvider.getByFileId(savedData.getUuid());
            if (savedData.getCount() == entries.size()) {
                updateEntryInDbAndSendEvent(savedData, FileState.PROCESSED);
            }
        } catch (Exception ex) {
            log.warn("Hit exception : {}", ex.getMessage());
        }
    }

    private RawDataEntry validateRow(RawDataEntry entry) {
        if (Objects.isNull(entry)) {
            return null;
        }

        val emptyValues = entry.getData().stream()
                .anyMatch(String::isEmpty);
        if (emptyValues) {
            return null;
        }

        //TODO: Add schema validation
        return entry;

    }

    private List<RawDataEntry> getRows(String fileId, byte[] data) {
        val splitData = new String(data, StandardCharsets.UTF_8).split(LINE_SEPARATOR);

        // Skipping header row
        val startIndex = 1;
        return IntStream.range(startIndex, splitData.length)
                .mapToObj(val -> RawDataEntry.builder()
                        .data(getTokens(splitData[val]))
                        .fileIndex(val)
                        .fileId(fileId)
                        .partitionId(CommonUtils.getRawDataPartitionId(val))
                        .build())
                .collect(Collectors.toList());
    }

    //TODO: Use final variables for string literals
    private List<String> getTokens(String input) {
        return Arrays.stream(input.split(ENTRY_SEPARATOR))
                .map(String::trim)
                .map(entry -> entry.replace(" ", "_"))
                .collect(Collectors.toList());
    }

    private FileData saveEntryInDbAndSendEvent(FileData data) {
        val savedData = dbCommand.save(data);

        //TODO: Send event based on error check
        return savedData.get();
    }

    private FileData updateEntryInDbAndSendEvent(FileData data, FileState state) {
        data.setState(state);
        val savedData = dbCommand.update(data);

        //TODO: Send event based on error check
        return savedData.get();
    }
}
