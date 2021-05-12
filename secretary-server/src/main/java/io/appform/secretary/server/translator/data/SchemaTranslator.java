package io.appform.secretary.server.translator.data;

import com.fasterxml.jackson.core.type.TypeReference;
import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.model.schema.Schema;
import io.appform.secretary.server.dao.StoredValidationSchema;
import io.appform.secretary.server.utils.MapperUtils;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class SchemaTranslator {

    public CellSchema toDto(StoredValidationSchema dao) {
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

    public StoredValidationSchema toDao(CellSchema dto) {
        return StoredValidationSchema.builder()
                .uuid(dto.getUuid())
                .name(dto.getName())
                .description(dto.getDescription())
                .tag(dto.getTag())
                .active(dto.isActive())
                .validators(MapperUtils.serialize(dto.getSchemas()))
                .build();
    }

}
