package io.appform.secretary.model.schema.impl;

import io.appform.secretary.model.schema.SchemaHandler;
import io.appform.secretary.model.schema.Schema;
import io.appform.secretary.model.schema.ValidationType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ListSchema extends Schema {

    private List<String> values;

    @Builder
    public ListSchema(List<String> values, String tag) {
        super(ValidationType.VALIDATION_LIST, tag);
        this.values = values;
    }

    @Override
    public <T> T visit(SchemaHandler<T> handler) {
        return handler.handle(this);
    }
}
