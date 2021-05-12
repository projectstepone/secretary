package io.appform.secretary.server.command;

import io.appform.secretary.model.schema.Schema;

import java.util.List;
import java.util.Optional;

public interface ValidationSchemaProvider {

    Optional<Schema> save(Schema schema);

    Optional<Schema> update(Schema schema);

    Optional<Schema> get(String uuid);

    List<Schema> getAll();
}
