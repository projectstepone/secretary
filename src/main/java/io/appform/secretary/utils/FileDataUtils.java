package io.appform.secretary.utils;

import io.appform.secretary.dao.StoredFileData;
import io.appform.secretary.model.FileData;
import io.appform.secretary.model.state.FileState;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileDataUtils {

    public FileData toDto(StoredFileData fileData) {
        return FileData.builder()
                .uuid(fileData.getUuid())
                .user(fileData.getUser())
                .name(fileData.getName())
                .hash(fileData.getHash())
                .workflow(fileData.getWorkflow())
                .state(FileState.valueOf(fileData.getState()))
                .build();
    }

    public StoredFileData toDao(FileData fileData) {
        return StoredFileData.builder()
                .uuid(fileData.getUuid())
                .name(fileData.getName())
                .hash(fileData.getHash())
                .state(fileData.getState().getValue())
                .user(fileData.getUser())
                .workflow(fileData.getWorkflow())
                .build();
    }
}
