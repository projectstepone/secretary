package io.appform.secretary.model.schema;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.appform.secretary.model.schema.impl.ListSchema;
import io.appform.secretary.model.schema.impl.RangeSchema;
import io.appform.secretary.model.schema.impl.RegexSchema;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ListSchema.class, name = ValidationType.VALIDATION_LIST_TEXT),
        @JsonSubTypes.Type(value = RangeSchema.class, name = ValidationType.VALIDATION_RANGE_TEXT),
        @JsonSubTypes.Type(value = RegexSchema.class, name = ValidationType.VALIDATION_REGEX_TEXT)
})
public abstract class Schema {
    @NotNull
    private final ValidationType type;
    @NotNull
    @NotBlank
    private String tag;

    protected Schema(ValidationType type, String tag) {
        this.type = type;
        this.tag = tag;
    }

    public abstract <T> T visit(SchemaHandler<T> handler);
}
