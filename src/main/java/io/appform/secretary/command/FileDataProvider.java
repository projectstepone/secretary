package io.appform.secretary.command;

import io.appform.secretary.model.FileData;

import java.util.List;
import java.util.Optional;

public interface FileDataProvider {

    public Optional<FileData> get(String uuid);

    public List<FileData> getAll();

    public List<FileData> getByHashValue(String hashValue);

    public List<FileData> getByUser(String hashValue);

    public void save(FileData fileData);

    public Optional<FileData> update(FileData fileData);

}
