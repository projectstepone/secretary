package io.appform.secretary.server.translator.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.inject.Singleton;
import io.appform.secretary.model.schema.Schema;
import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.server.dao.StoredCellSchema;
import io.appform.secretary.server.utils.MapperUtils;

import java.util.List;

@Singleton
public class CellSchemaTranslator {

    public CellSchema toDto(StoredCellSchema dao) {
        return CellSchema.builder()
                .uuid(dao.getUuid())
                .name(dao.getName())
                .description(dao.getDescription())
                .tag(dao.getTag())
                .active(dao.isActive())
                .schemas(MapperUtils.deserialize(dao.getValidators(),
                        new TypeReference<List<Schema>>() {}))
                .build();
    }

    public StoredCellSchema toDao(CellSchema dto) {
        return StoredCellSchema.builder()
                .uuid(dto.getUuid())
                .name(dto.getName())
                .description(dto.getDescription())
                .tag(dto.getTag())
                .active(dto.isActive())
                .validators(MapperUtils.serialize(dto.getSchemas()))
                .build();
    }

}
