package io.appform.secretary.server.utils;

import io.appform.secretary.model.FileData;
import io.appform.secretary.model.state.FileState;
import io.appform.secretary.server.dao.StoredFileData;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileDataUtils{

    public FileData toDto(StoredFileData dao) {
        return FileData.builder()
                .uuid(dao.getUuid())
                .user(dao.getUser())
                .name(dao.getName())
                .hash(dao.getHash())
                .workflow(dao.getWorkflow())
                .count(dao.getEntryCount())
                .state(FileState.get(dao.getState()))
                .createdAt(dao.getCreated().toLocaleString())
                .build();
    }

    public StoredFileData toDao(FileData dto) {
        return StoredFileData.builder()
                .uuid(dto.getUuid())
                .name(dto.getName())
                .hash(dto.getHash())
                .state(dto.getState().getValue())
                .user(dto.getUser())
                .entryCount(dto.getCount())
                .workflow(dto.getWorkflow())
                .build();
    }
}
