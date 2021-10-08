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
public class RegexSchema extends Schema {

    @NotNull
    @NotBlank
    private String regex;

    @Builder
    public RegexSchema(String tag, String regex) {
        super(ValidationType.VALIDATION_REGEX, tag);
        this.regex = regex;
    }

    @Override
    public <T> T visit(SchemaHandler<T> handler) {
        return handler.handle(this);
    }
}
