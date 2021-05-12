package io.appform.secretary.server.validator;

import com.google.inject.Singleton;
import io.appform.secretary.model.RawDataEntry;
import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.model.schema.file.FileSchema;
import io.appform.secretary.server.executor.ValidationExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.inject.Inject;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RowValidator {

    private final ValidationExecutor validationExecutor;

    public RawDataEntry validateRow(RawDataEntry entry, FileSchema schema) {
        if (Objects.isNull(entry)) {
            return null;
        }

        val emptyValues = entry.getData().stream()
                .anyMatch(String::isEmpty);
        if (emptyValues) {
            log.warn("Empty string detected : {}", entry);
            return null;
        }

        val schemaSize = schema.getCellSchema().size();
        val dataSize = entry.getData().size();
        if (schemaSize != dataSize) {
            log.warn("Schema and data mismatch : schema entries {} Data entries: {}", schemaSize, dataSize);
            return null;
        }

        val invalid = IntStream.range(0, schema.getCellSchema().size())
                .mapToObj(index -> validateEntry(schema.getCellSchema().get(index),
                        entry.getData().get(index)))
                .anyMatch(this::isFalse);

        //TODO: Convert row to key-value pair
        return invalid ? null : entry;
    }

    private boolean validateEntry(CellSchema cellSchema, String entry) {
        return validationExecutor.validate(cellSchema, entry);
    }

    private boolean isFalse(boolean bool) {
        return !bool;
    }

}
