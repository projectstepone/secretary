package io.appform.secretary.server.translator.request;

import com.google.inject.Singleton;
import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.model.schema.cell.request.CreateRequest;
import io.appform.secretary.model.schema.cell.request.UpdateRequest;

import java.util.Objects;

@Singleton
public class CellSchemaTranslator {

    public CellSchema toSchema(CreateRequest request) {
        return CellSchema.builder()
                .active(true)
                .name(request.getName())
                .description(request.getDescription())
                .tag(request.getTag())
                .schemas(request.getSchemas())
                .build();
    }

    public CellSchema toSchema(UpdateRequest request, CellSchema cellSchema) {
        cellSchema.setActive(request.isActive());
        if (!Objects.isNull(request.getSchemas())) {
            cellSchema.setSchemas(request.getSchemas());
        }
        return cellSchema;
    }
}
