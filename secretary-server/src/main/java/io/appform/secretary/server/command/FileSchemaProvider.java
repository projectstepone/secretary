package io.appform.secretary.server.command;

import io.appform.secretary.model.schema.file.FileSchema;

import java.util.List;
import java.util.Optional;

public interface FileSchemaProvider {

    Optional<FileSchema> save(FileSchema schema);

    Optional<FileSchema> update(FileSchema schema);

    Optional<FileSchema> get(String uuid);

    List<FileSchema> getAll();
}
