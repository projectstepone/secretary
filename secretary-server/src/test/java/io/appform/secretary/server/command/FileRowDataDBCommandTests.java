package io.appform.secretary.server.command;

import com.google.inject.Inject;
import io.appform.secretary.model.RawDataEntry;
import io.appform.secretary.server.TestBase;
import io.appform.secretary.server.command.impl.FileRowDataDBCommand;
import io.appform.secretary.server.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class FileRowDataDBCommandTests extends TestBase {

    @Inject
    private FileRowDataDBCommand fileRowDataDBCommand;

    @Test
    void testSave() {
        val rawEntry = RawDataEntry.builder()
                .data(new HashMap<>())
                .fileId(UUID.randomUUID().toString())
                .fileIndex(9)
                .partitionId(3)
                .build();
        assertTrue(fileRowDataDBCommand.save(rawEntry).isPresent());
    }

    @Test
    void testGetByFileId() {
        val fileId = UUID.randomUUID().toString();
        val rawEntry1 = RawDataEntry.builder()
                .data(new HashMap<>())
                .fileId(fileId)
                .fileIndex(9)
                .partitionId(3)
                .build();
        val rawEntry2 = RawDataEntry.builder()
                .data(new HashMap<>())
                .fileId(fileId)
                .fileIndex(2)
                .partitionId(4)
                .build();
        assertTrue(fileRowDataDBCommand.save(rawEntry1).isPresent());
        assertTrue(fileRowDataDBCommand.save(rawEntry2).isPresent());
        val rawEntries = fileRowDataDBCommand.getByFileId(fileId);
        assertEquals(2, rawEntries.size());
    }

    @Test
    void testGetByKey() {
        val rawEntry = RawDataEntry.builder()
                .data(new HashMap<>())
                .fileId(UUID.randomUUID().toString())
                .fileIndex(12)
                .partitionId(3)
                .build();
        assertTrue(fileRowDataDBCommand.save(rawEntry).isPresent());
        assertTrue(fileRowDataDBCommand.get(CommonUtils.getKey(rawEntry.getFileId(), rawEntry.getFileIndex())).isPresent());
    }
}
