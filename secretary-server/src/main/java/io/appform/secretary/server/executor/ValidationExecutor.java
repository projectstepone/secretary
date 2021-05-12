package io.appform.secretary.server.executor;

import com.google.inject.Singleton;
import io.appform.secretary.model.schema.impl.ListValidationSchema;
import io.appform.secretary.model.schema.impl.RangeValidationSchema;
import io.appform.secretary.model.schema.impl.RegexValidationSchema;
import io.appform.secretary.model.schema.Schema;
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
    private static final String SCHEMA_COUNT_MISMATCH = "Validator count mismatch for schema {} to validate input {}";

    public boolean validate(Schema schema, String input) {
        try {
            if (Objects.isNull(schema) || Objects.isNull(schema.getSchemas()) || Objects.isNull(input)) {
                return false;
            }

            val mode = ValidationMode.get(schema.getTag());
            return mode.visit(new ValidationMode.ValidationModeVisitor<Boolean>() {

                @Override
                public Boolean visitNoCheck() {
                    return true;
                }

                @Override
                public Boolean visitInList() {
                    return validateInList(schema, input);
                }

                @Override
                public Boolean visitInRangeInt() {
                    return validateInRangeInt(schema, input);
                }

                @Override
                public Boolean visitMatchRegex() {
                    return validateMatchRegex(schema, input);
                }
            });
        } catch (Exception ex) {
            log.error("Failed to validate input [{}] with schema {}:", input, schema);
            return false;
        }
    }

    private boolean validateInList(Schema schema, String input) {
        if (schema.getSchemas().size() != SINGLE_SCHEMA) {
            log.warn(SCHEMA_COUNT_MISMATCH, schema, input);
            return false;
        }

        if (schema.getSchemas().get(0) instanceof ListValidationSchema) {
            val listSchema =  (ListValidationSchema) schema.getSchemas().get(0);

            return listSchema.getValues().stream()
                    .anyMatch(entry -> StringUtils.equals(entry.trim(), input.trim()));
        } else {
            return false;
        }
    }

    private boolean validateInRangeInt(Schema schema, String input) {
        if (schema.getSchemas().size() != SINGLE_SCHEMA) {
            log.warn(SCHEMA_COUNT_MISMATCH, schema, input);
            return false;
        }

        if (schema.getSchemas().get(0) instanceof RangeValidationSchema) {
            val rangeSchema =  (RangeValidationSchema) schema.getSchemas().get(0);

            val start = Integer.parseInt(rangeSchema.getStart());
            val end = Integer.parseInt(rangeSchema.getEnd());
            val entry = Integer.parseInt(input);

            return (start <= entry) && (entry <= end);
        } else {
            return false;
        }
    }

    private boolean validateMatchRegex(Schema schema, String input) {
        if (schema.getSchemas().size() != SINGLE_SCHEMA) {
            log.warn(SCHEMA_COUNT_MISMATCH, schema, input);
            return false;
        }

        if (schema.getSchemas().get(0) instanceof RegexValidationSchema) {
            val regexSchema =  (RegexValidationSchema) schema.getSchemas().get(0);
            val pattern = Pattern.compile(regexSchema.getRegex());
            val matcher = pattern.matcher(input);

            return matcher.matches();
        } else {
            return false;
        }
    }
}
