package io.appform.secretary.server.command;

import io.appform.secretary.server.model.ValidationSchema;

import java.util.List;
import java.util.Optional;

public interface ValidationSchemaProvider {

    Optional<ValidationSchema> createSchema(ValidationSchema schema);

    Optional<ValidationSchema> updateSchema(ValidationSchema schema);

    Optional<ValidationSchema> getSchema(String uuid);

    List<ValidationSchema> getAllSchema();

    Optional<ValidationSchema> disableSchema(String uuid);
}
