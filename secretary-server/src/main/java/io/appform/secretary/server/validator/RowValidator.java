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

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RowValidator {

    private final ValidationExecutor validationExecutor;

    public RawDataEntry validateRow(RawDataEntry entry, FileSchema schema) {
        if (Objects.isNull(entry)) {
            return null;
        }

        val schemaSize = schema.getCellSchema().size();
        val dataSize = entry.getData().size();

        // we add two extra fields now and wfSource
        if (schemaSize != (dataSize - 2)) {
            log.warn("Schema and data mismatch : schema entries {} Data entries: {} workflow: {}", schemaSize, dataSize, schema.getWorkflow().getName());
            return null;
        }

        val invalid = schema.getCellSchema().stream().map(cellSchema -> validateEntry(cellSchema, entry.getData().get(cellSchema.getName())))
                .anyMatch(this::isFalse);

        return invalid ? null : entry;
    }

    private boolean validateEntry(CellSchema cellSchema, String entry) {
        return validationExecutor.validate(cellSchema, entry);
    }

    private boolean isFalse(boolean bool) {
        return !bool;
    }

}
