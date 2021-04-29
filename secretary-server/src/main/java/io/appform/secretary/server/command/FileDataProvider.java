package io.appform.secretary.server.command;

import io.appform.secretary.model.FileData;

import java.util.List;
import java.util.Optional;

public interface FileDataProvider {

    Optional<FileData> get(String uuid);

    List<FileData> getAll();

    Optional<FileData> getByHashValue(String hashValue);

    List<FileData> getByUser(String hashValue);

    Optional<FileData> save(FileData fileData);

    Optional<FileData> update(FileData fileData);

}
