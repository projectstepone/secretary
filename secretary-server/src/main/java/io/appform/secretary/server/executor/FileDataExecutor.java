package io.appform.secretary.server.executor;

import com.google.inject.Singleton;
import io.appform.secretary.model.FileData;
import io.appform.secretary.model.RawDataEntry;
import io.appform.secretary.model.configuration.SecretaryConfiguration;
import io.appform.secretary.model.state.FileState;
import io.appform.secretary.server.command.FileRowDataProvider;
import io.appform.secretary.server.command.FileSchemaProvider;
import io.appform.secretary.server.command.KafkaProducerCommand;
import io.appform.secretary.server.command.impl.FileDataDBCommand;
import io.appform.secretary.model.fileschema.FileSchema;
import io.appform.secretary.server.internal.model.InputFileData;
import io.appform.secretary.server.internal.model.KafkaMessage;
import io.appform.secretary.model.schema.Schema;
import io.appform.secretary.server.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileDataExecutor implements DataExecutor {

    private static final String ENTRY_SEPARATOR = ",";
    private static final String LINE_SEPARATOR = "\n";

    private final SecretaryConfiguration serviceConfig;
    private final KafkaProducerCommand kafkaProducer;
    private final FileDataDBCommand dbCommand;
    private final FileRowDataProvider rowDataProvider;
    private final FileSchemaProvider fileSchemaProvider;
    private final ValidationExecutor validationExecutor;

    @Override
    public void processFile(InputFileData inputData) {
        try {
            var data = FileData.builder()
                    .name(inputData.getFile())
                    .workflow(inputData.getWorkflow())
                    .user(inputData.getUser())
                    .state(FileState.ACCEPTED)
                    .hash(inputData.getHash())
                    .build();

            if (!inputData.isRetry()) {
                data = saveEntryInDbAndSendEvent(data);
            } else {
                data.setUuid(inputData.getUuid());
            }

            val validEntries = getValidEntries(data, inputData);
            log.info("File {} has {} valid entries", data.getName(), validEntries.size());

            if (validEntries.size() == 0) {
                data = updateEntryInDbAndSendEvent(data, FileState.SKIPPED);
                log.warn("File has no valid entries: {}", data.getUuid());
                return;
            }

            if (!inputData.isRetry()) {
                data = updateEntryInDbAndSendEvent(data, FileState.PROCESSING);
            }

            ingestInKafka(validEntries);

            val entries = rowDataProvider.getByFileId(data.getUuid());
            if (data.getCount() == entries.size()) {
                updateEntryInDbAndSendEvent(data, FileState.PROCESSED);
            }
        } catch (Exception ex) {
            log.warn("Hit exception : {}", ex.getMessage());
        }
    }

    private void ingestInKafka(List<RawDataEntry> entries) {
        var count = new AtomicInteger(0);
        entries.forEach(entry -> {
            //TODO: Add cache to ease load on DB
            val savedData = rowDataProvider.save(entry);
            if (savedData.isPresent()) {
                val message = KafkaMessage.builder()
                        .topic(serviceConfig.getKafkaTopic())
                        .key(UUID.randomUUID().toString())
                        .value(entry.getData().toString())
                        .build();
                kafkaProducer.push(message);
                count.getAndIncrement();
            }
        });

        log.info("Ingested {} entries", count.get());
    }

    private FileSchema getFileSchema(String workflow) {
        val schema = fileSchemaProvider.get(workflow);
        return schema.orElse(null);
    }

    private List<RawDataEntry> getValidEntries(FileData file, InputFileData input) {
        val fileSchema = getFileSchema(file.getWorkflow());
        if (Objects.isNull(fileSchema)) {
            log.warn("Unable to find file schema for workflow: {}", file.getWorkflow());
            return Collections.emptyList();
        }

        val dataEntries = getRows(file.getUuid(), input.getContent());
        val validEntries = dataEntries.stream()
                .map(entry -> validateRow(entry, fileSchema))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        file.setCount(validEntries.size());
        return validEntries;
    }

    private boolean validateEntry(Schema schema, String entry) {
        return validationExecutor.validate(schema, entry);
    }

    private boolean isFalse(boolean bool) {
        return !bool;
    }

    private RawDataEntry validateRow(RawDataEntry entry, FileSchema schema) {
        if (Objects.isNull(entry)) {
            return null;
        }

        val emptyValues = entry.getData().stream()
                .anyMatch(String::isEmpty);
        if (emptyValues) {
            log.warn("Empty string detected : {}", entry);
            return null;
        }

        val schemaSize = schema.getSchema().size();
        val dataSize = entry.getData().size();
        if (schemaSize != dataSize) {
            log.warn("Schema and data mismatch : Schema entries {} Data entries: {}", schemaSize, dataSize);
            return null;
        }

        val invalid = IntStream.range(0, schema.getSchema().size())
                .mapToObj(index -> validateEntry(schema.getSchema().get(index), entry.getData().get(index)))
                .anyMatch(this::isFalse);

        //TODO: Convert row to key-value pair
        return invalid ? null : entry;
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
