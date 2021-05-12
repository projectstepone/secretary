package io.appform.secretary.server.translator.request;

import io.appform.secretary.model.schema.request.NewSchemaRequest;
import io.appform.secretary.model.schema.request.UpdateSchemaRequest;
import io.appform.secretary.model.schema.Schema;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class SchemaRequestTranslator {

    public Schema createSchema(NewSchemaRequest request) {
        return Schema.builder()
                .active(true)
                .name(request.getName())
                .description(request.getDescription())
                .tag(request.getTag())
                .schemas(request.getSchema())
                .build();
    }

    public Schema updateSchema(UpdateSchemaRequest request, Schema schema) {
        schema.setActive(request.isValid());
        if (!Objects.isNull(request.getSchema())) {
            schema.setSchemas(request.getSchema());
        }
        return schema;
    }
}
