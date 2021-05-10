package io.appform.secretary.server.translator;

import io.appform.secretary.model.validationschema.NewSchemaRequest;
import io.appform.secretary.model.validationschema.UpdateSchemaRequest;
import io.appform.secretary.server.internal.model.Schema;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class SchemaRequestTranslator {

    public Schema createSchema(NewSchemaRequest request) {
        return Schema.builder()
                .active(true)
                .name(request.getName())
                .description(request.getDescription())
                .schema(request.getSchema())
                .build();
    }

    public Schema updateSchema(UpdateSchemaRequest request, Schema schema) {
        schema.setActive(request.isValid());
        if (!Objects.isNull(request.getSchema())) {
            schema.setSchema(request.getSchema());
        }
        return schema;
    }
}
