package io.appform.secretary.server.command;

import io.appform.secretary.server.model.FileData;

import java.util.List;
import java.util.Optional;

public interface FileDataProvider {

    public Optional<FileData> get(String uuid);

    public List<FileData> getAll();

    public Optional<FileData> getByHashValue(String hashValue);

    public List<FileData> getByUser(String hashValue);

    public Optional<FileData> save(FileData fileData);

    public Optional<FileData> update(FileData fileData);

}
