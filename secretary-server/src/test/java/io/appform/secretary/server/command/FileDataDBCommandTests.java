package io.appform.secretary.server.command;

import com.google.inject.Inject;
import io.appform.secretary.model.FileData;
import io.appform.secretary.model.state.FileState;
import io.appform.secretary.server.TestBase;
import io.appform.secretary.server.command.impl.FileDataDBCommand;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class FileDataDBCommandTests extends TestBase {

    @Inject
    private FileDataDBCommand fileDataDBCommand;

    @Test
    void testSaveFileData() {
        val fileData = FileData.builder()
                .count(2L)
                .hash(UUID.randomUUID().toString())
                .name("name")
                .uuid(UUID.randomUUID().toString())
                .state(FileState.ACCEPTED)
                .user("user")
                .workflow("workflow")
                .build();
        assertTrue(fileDataDBCommand.save(fileData).isPresent());
    }

    @Test
    void testUpdateFileData() {
        val fileData = FileData.builder()
                .count(2L)
                .hash(UUID.randomUUID().toString())
                .name("name")
                .uuid(UUID.randomUUID().toString())
                .state(FileState.ACCEPTED)
                .user("user")
                .workflow("workflow")
                .build();
        val optionalFileData = fileDataDBCommand.save(fileData);
        assertTrue(optionalFileData.isPresent());
        val fileDataUpdated =  optionalFileData.get();
        fileDataUpdated.setState(FileState.PROCESSING);
        assertTrue(fileDataDBCommand.update(fileDataUpdated).isPresent());
        val optionalUpdatedData = fileDataDBCommand.getFromDb(fileDataUpdated.getUuid());
        assertTrue(optionalUpdatedData.isPresent());
        assertEquals(FileState.PROCESSING, optionalUpdatedData.get().getState());
    }

    @Test
    void testGetByUser() {
        val user = UUID.randomUUID().toString();
        val fileData1 = FileData.builder()
                .count(2L)
                .hash(UUID.randomUUID().toString())
                .name("name")
                .uuid(UUID.randomUUID().toString())
                .state(FileState.ACCEPTED)
                .user(user)
                .workflow("workflow")
                .build();
        val fileData2 = FileData.builder()
                .count(2L)
                .hash(UUID.randomUUID().toString())
                .name("name")
                .uuid(UUID.randomUUID().toString())
                .state(FileState.ACCEPTED)
                .user(user)
                .workflow("workflow")
                .build();
        fileDataDBCommand.save(fileData1);
        fileDataDBCommand.save(fileData2);
        val fileUploads = fileDataDBCommand.getByUser("user");
        assertEquals(2, fileUploads.size());
    }

    @Test
    void testGetByHashValue() {
        val user = UUID.randomUUID().toString();
        val hash = UUID.randomUUID().toString();
        val fileData = FileData.builder()
                .count(2L)
                .hash(hash)
                .name("name")
                .uuid(UUID.randomUUID().toString())
                .state(FileState.ACCEPTED)
                .user(user)
                .workflow("workflow")
                .build();
        fileDataDBCommand.save(fileData);
        val fileUploads = fileDataDBCommand.getByHashValue(hash);
        assertTrue(fileUploads.isPresent());
    }
}
