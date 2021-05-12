package io.appform.secretary.server.translator.request;

import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.model.schema.cell.request.CreateSchemaRequest;
import io.appform.secretary.model.schema.cell.request.UpdateSchemaRequest;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class SchemaRequestTranslator {

    public CellSchema createSchema(CreateSchemaRequest request) {
        return CellSchema.builder()
                .active(true)
                .name(request.getName())
                .description(request.getDescription())
                .tag(request.getTag())
                .schemas(request.getSchemas())
                .build();
    }

    public CellSchema updateSchema(UpdateSchemaRequest request, CellSchema cellSchema) {
        cellSchema.setActive(request.isValid());
        if (!Objects.isNull(request.getSchemas())) {
            cellSchema.setSchemas(request.getSchemas());
        }
        return cellSchema;
    }
}
