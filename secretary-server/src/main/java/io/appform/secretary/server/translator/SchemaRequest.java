package io.appform.secretary.server.translator;

import io.appform.secretary.model.validationschema.NewSchemaRequest;
import io.appform.secretary.server.internal.model.Schema;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SchemaRequest {

    public Schema createSchema(NewSchemaRequest request) {
        return Schema.builder()
                .active(true)
                .name(request.getName())
                .description(request.getDescription())
                .schema(request.getSchema())
                .build();
    }
}
