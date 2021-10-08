package io.appform.secretary.model.schema.impl;

import io.appform.secretary.model.schema.SchemaHandler;
import io.appform.secretary.model.schema.Schema;
import io.appform.secretary.model.schema.ValidationType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RangeSchema extends Schema {
    @NotNull
    @NotBlank
    private String start;
    @NotNull
    @NotBlank
    private String end;

    @Builder
    public RangeSchema(String start, String end, String tag) {
        super(ValidationType.VALIDATION_RANGE, tag);
        this.start = start;
        this.end = end;
    }

    @Override
    public <T> T visit(SchemaHandler<T> handler) {
        return handler.handle(this);
    }
}
