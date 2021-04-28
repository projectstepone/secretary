package io.appform.secretary.server.utils;

import io.appform.secretary.server.dao.StoredFileData;
import io.appform.secretary.server.model.FileData;
import io.appform.secretary.server.model.state.FileState;
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
                .state(FileState.get(dao.getState()))
                .build();
    }

    public StoredFileData toDao(FileData dto) {
        return StoredFileData.builder()
                .uuid(dto.getUuid())
                .name(dto.getName())
                .hash(dto.getHash())
                .state(dto.getState().getValue())
                .user(dto.getUser())
                .workflow(dto.getWorkflow())
                .build();
    }
}
