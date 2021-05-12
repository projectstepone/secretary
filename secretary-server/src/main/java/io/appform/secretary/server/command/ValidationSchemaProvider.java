package io.appform.secretary.server.command;

import io.appform.secretary.model.schema.cell.CellSchema;

import java.util.List;
import java.util.Optional;

public interface ValidationSchemaProvider {

    Optional<CellSchema> save(CellSchema cellSchema);

    Optional<CellSchema> update(CellSchema cellSchema);

    Optional<CellSchema> get(String uuid);

    List<CellSchema> getAll();
}
