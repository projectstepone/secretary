package io.appform.secretary.server.translator;

import com.fasterxml.jackson.core.type.TypeReference;
import io.appform.secretary.model.schema.ValidationSchema;
import io.appform.secretary.server.dao.StoredValidationSchema;
import io.appform.secretary.server.internal.model.Schema;
import io.appform.secretary.server.utils.MapperUtils;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class SchemaTranslator {

    public Schema toDto(StoredValidationSchema dao) {
        return Schema.builder()
                .uuid(dao.getUuid())
                .name(dao.getName())
                .description(dao.getDescription())
                .active(dao.isActive())
                .schema(MapperUtils.deserialize(dao.getValidators(),
                        new TypeReference<List<ValidationSchema>>() {}))
                .build();
    }

    public StoredValidationSchema toDao(Schema dto) {
        return StoredValidationSchema.builder()
                .uuid(dto.getUuid())
                .name(dto.getName())
                .description(dto.getDescription())
                .active(dto.isActive())
                .validators(MapperUtils.serialize(dto.getSchema()))
                .build();
    }

}
