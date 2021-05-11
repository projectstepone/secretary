package io.appform.secretary.model.schema.impl;

import io.appform.secretary.model.schema.SchemaHandler;
import io.appform.secretary.model.schema.ValidationSchema;
import io.appform.secretary.model.schema.ValidationType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RangeValidationSchema extends ValidationSchema {

    private String start;
    private String end;

    @Builder
    public RangeValidationSchema(String start, String end, String tag) {
        super(ValidationType.VALIDATION_RANGE, tag);
        this.start = start;
        this.end = end;
    }

    @Override
    public <T> T visit(SchemaHandler<T> handler) {
        return handler.handle(this);
    }
}
