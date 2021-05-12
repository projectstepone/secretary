package io.appform.secretary.server.executor;

import com.google.inject.Singleton;
import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.model.schema.impl.ListSchema;
import io.appform.secretary.model.schema.impl.RangeSchema;
import io.appform.secretary.model.schema.impl.RegexSchema;
import io.appform.secretary.model.schema.ValidationMode;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@Singleton
public class ValidationExecutor {

    private static final int SINGLE_SCHEMA = 1;
    private static final String SCHEMA_COUNT_MISMATCH = "Validator count mismatch for cellSchema {} to validate input {}";

    public boolean validate(CellSchema cellSchema, String input) {
        try {
            if (Objects.isNull(cellSchema) || Objects.isNull(cellSchema.getSchemas()) || Objects.isNull(input)) {
                return false;
            }

            val mode = ValidationMode.get(cellSchema.getTag());
            return mode.visit(new ValidationMode.ValidationModeVisitor<Boolean>() {

                @Override
                public Boolean visitNoCheck() {
                    return true;
                }

                @Override
                public Boolean visitInList() {
                    return validateInList(cellSchema, input);
                }

                @Override
                public Boolean visitInRangeInt() {
                    return validateInRangeInt(cellSchema, input);
                }

                @Override
                public Boolean visitMatchRegex() {
                    return validateMatchRegex(cellSchema, input);
                }
            });
        } catch (Exception ex) {
            log.error("Failed to validate input [{}] with cellSchema {}:", input, cellSchema);
            return false;
        }
    }

    private boolean validateInList(CellSchema cellSchema, String input) {
        if (cellSchema.getSchemas().size() != SINGLE_SCHEMA) {
            log.warn(SCHEMA_COUNT_MISMATCH, cellSchema, input);
            return false;
        }

        if (cellSchema.getSchemas().get(0) instanceof ListSchema) {
            val listSchema =  (ListSchema) cellSchema.getSchemas().get(0);

            return listSchema.getValues().stream()
                    .anyMatch(entry -> StringUtils.equals(entry.trim(), input.trim()));
        } else {
            return false;
        }
    }

    private boolean validateInRangeInt(CellSchema cellSchema, String input) {
        if (cellSchema.getSchemas().size() != SINGLE_SCHEMA) {
            log.warn(SCHEMA_COUNT_MISMATCH, cellSchema, input);
            return false;
        }

        if (cellSchema.getSchemas().get(0) instanceof RangeSchema) {
            val rangeSchema =  (RangeSchema) cellSchema.getSchemas().get(0);

            val start = Integer.parseInt(rangeSchema.getStart());
            val end = Integer.parseInt(rangeSchema.getEnd());
            val entry = Integer.parseInt(input);

            return (start <= entry) && (entry <= end);
        } else {
            return false;
        }
    }

    private boolean validateMatchRegex(CellSchema cellSchema, String input) {
        if (cellSchema.getSchemas().size() != SINGLE_SCHEMA) {
            log.warn(SCHEMA_COUNT_MISMATCH, cellSchema, input);
            return false;
        }

        if (cellSchema.getSchemas().get(0) instanceof RegexSchema) {
            val regexSchema =  (RegexSchema) cellSchema.getSchemas().get(0);
            val pattern = Pattern.compile(regexSchema.getRegex());
            val matcher = pattern.matcher(input);

            return matcher.matches();
        } else {
            return false;
        }
    }
}
