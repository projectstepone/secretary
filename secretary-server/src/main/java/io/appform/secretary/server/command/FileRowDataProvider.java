package io.appform.secretary.server.command;

import io.appform.secretary.model.RawDataEntry;

import java.util.List;
import java.util.Optional;

public interface FileRowDataProvider {

    Optional<RawDataEntry> get(String key);

    List<RawDataEntry> getByFileId(String fileId);

    Optional<RawDataEntry> save(RawDataEntry entry);
}
