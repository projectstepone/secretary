package io.appform.secretary.server.executor;

import com.google.inject.Singleton;
import io.appform.secretary.model.FileData;
import io.appform.secretary.model.Pair;
import io.appform.secretary.model.RawDataEntry;
import io.appform.secretary.model.configuration.SecretaryConfiguration;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.model.schema.file.FileSchema;
import io.appform.secretary.model.state.FileState;
import io.appform.secretary.server.actors.ActorManager;
import io.appform.secretary.server.actors.ActorType;
import io.appform.secretary.server.actors.messages.RawDataEntryMessage;
import io.appform.secretary.server.command.FileDataProvider;
import io.appform.secretary.server.command.FileRowDataProvider;
import io.appform.secretary.server.command.FileSchemaProvider;
import io.appform.secretary.server.internal.model.InputFileData;
import io.appform.secretary.server.utils.CommonUtils;
import io.appform.secretary.server.validator.RowValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileDataExecutor implements DataExecutor {

    private final SecretaryConfiguration serviceConfig;
    private final FileDataProvider fileDataProvider;
    private final FileRowDataProvider rowDataProvider;
    private final FileSchemaProvider fileSchemaProvider;
    private final ActorManager actorManager;
    private final RowValidator validator;

    private static final String ENTRY_SEPARATOR = ",";
    private static final String LINE_SEPARATOR = "\n";

    @Override
    public FileData processFile(InputFileData inputData) {
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
                return data;
            }

            if (!inputData.isRetry()) {
                data = updateEntryInDbAndSendEvent(data, FileState.PROCESSING);
            }

            ingestToRmq(validEntries);

            val entries = rowDataProvider.getByFileId(data.getUuid());
            if (data.getCount() == entries.size()) {
                updateEntryInDbAndSendEvent(data, FileState.PROCESSED);
            }
            return data;
        } catch (Exception ex) {
            log.warn("Hit exception : {}", ex.getMessage());
            throw ex;
        }
    }

    private void ingestToRmq(List<RawDataEntry> entries) {
        var count = new AtomicInteger(0);
        entries.forEach(entry -> {
            val savedData = rowDataProvider.save(entry);
            if (savedData.isPresent()) {
                val message = RawDataEntryMessage.builder()
                        .rawDataEntry(entry)
                        .build();
                actorManager.publish(ActorType.FILE_ROW_PROCESSOR, message);
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
                .map(entry -> validator.validateRow(entry, fileSchema))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        file.setCount(validEntries.size());
        return validEntries;
    }

    private List<RawDataEntry> getRows(String fileId, byte[] data) {
        val splitData = new String(data, StandardCharsets.UTF_8).split(LINE_SEPARATOR);
        val headers = splitData[0];
        final List<String> headerValues = Arrays.stream(headers.split(ENTRY_SEPARATOR)).map(String::trim).collect(Collectors.toList());
        if (new HashSet<>(headerValues).size() != headerValues.size()) {
            log.info("duplicate headers in the file: {}", fileId);
            throw SecretaryError.propagate("Found duplicate header in the file", ResponseCode.BAD_REQUEST);
        }
        return IntStream.range(1, splitData.length)
                .mapToObj(val -> RawDataEntry.builder()
                        .data(getTokens(headerValues, splitData[val]))
                        .fileIndex(val)
                        .fileId(fileId)
                        .partitionId(CommonUtils.getRawDataPartitionId(val))
                        .build())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Map<String, String> getTokens(final List<String> headerValues, final String input) {
        final List<String> cellValues = Arrays.stream(input.split(ENTRY_SEPARATOR)).map(String::trim).collect(Collectors.toList());
        if (headerValues.size() != cellValues.size()) {
            log.error("Missing cell values: {} or headers: {}", cellValues, headerValues);
            return null;
        }
        return IntStream.range(0, headerValues.size())
                .mapToObj(cellIndex -> new Pair<>(headerValues.get(cellIndex), cellValues.get(cellIndex)))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private FileData saveEntryInDbAndSendEvent(FileData data) {
        val savedData = fileDataProvider.save(data);

        //TODO: Send event based on error check
        return savedData.get();
    }

    private FileData updateEntryInDbAndSendEvent(FileData data, FileState state) {
        data.setState(state);
        val savedData = fileDataProvider.update(data);

        //TODO: Send event based on error check
        return savedData.get();
    }
}
