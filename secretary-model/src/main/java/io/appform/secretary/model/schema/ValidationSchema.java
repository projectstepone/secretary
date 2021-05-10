package io.appform.secretary.model.schema;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.appform.secretary.model.schema.impl.ListValidationSchema;
import io.appform.secretary.model.schema.impl.RangeValidationSchema;
import io.appform.secretary.model.schema.impl.RegexValidationSchema;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ListValidationSchema.class, name = ValidationType.VALIDATION_LIST_TEXT),
        @JsonSubTypes.Type(value = RangeValidationSchema.class, name = ValidationType.VALIDATION_RANGE_TEXT),
        @JsonSubTypes.Type(value = RegexValidationSchema.class, name = ValidationType.VALIDATION_REGEX_TEXT)
})
public abstract class ValidationSchema {

    private final ValidationType type;
    private String tag;

    protected ValidationSchema(ValidationType type, String tag) {
        this.type = type;
        this.tag = tag;
    }

    public abstract <T> T visit(SchemaHandler<T> handler);
}
