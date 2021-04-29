package io.appform.secretary.server.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import io.appform.secretary.model.ValidationSchema;
import io.appform.secretary.model.validationschema.NewSchemaRequest;
import io.appform.secretary.model.validationschema.UpdateSchemaRequest;
import io.appform.secretary.server.dao.StoredValidationSchema;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ValidationSchemaUtils {

    private final int  INITIAL_SCHEMA_VERSION = 1;
    private final int INCREMENT_VERSION = 1;

    public ValidationSchema toSchema(StoredValidationSchema schema) {
        return ValidationSchema.builder()
                .active(schema.isActive())
                .name(schema.getName())
                .version(schema.getVersion())
                .uuid(schema.getUuid())
                .schema(MapperUtils.deserialize(schema.getSchema(), new TypeReference<Object>() {}))
                .build();
    }

    public StoredValidationSchema toDao(ValidationSchema schema) {
        return StoredValidationSchema.builder()
                .active(schema.isActive())
                .name(schema.getName())
                .version(schema.getVersion())
                .uuid(schema.getUuid())
                .schema(MapperUtils.serialize(schema.getSchema()))
                .build();
    }

    // TODO : Use translator object
    // TODO : Use hash of name and version as UUID
    public ValidationSchema toSchema(NewSchemaRequest request) {
        return ValidationSchema.builder()
                .uuid(UUID.randomUUID().toString())
                .name(request.getName())
                .version(INITIAL_SCHEMA_VERSION)
                .active(true)
                .schema(request.getSchema())
                .build();
    }

    public ValidationSchema updateSchema(ValidationSchema oldSchema, UpdateSchemaRequest request) {
        return ValidationSchema.builder()
                .uuid(oldSchema.getUuid())
                .name(oldSchema.getName())
                .version(oldSchema.getVersion() + INCREMENT_VERSION)
                .active(oldSchema.isActive())
                .schema(request.getSchema())
                .build();
    }

}
