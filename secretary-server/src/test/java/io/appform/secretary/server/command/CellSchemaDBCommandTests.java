package io.appform.secretary.server.command;

import com.google.inject.Inject;
import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.server.TestBase;
import io.appform.secretary.server.command.impl.CellSchemaDBCommand;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class CellSchemaDBCommandTests extends TestBase {

    @Inject
    private CellSchemaDBCommand cellSchemaDBCommand;

    @Test
    void testSave() {
        val cell = CellSchema.builder()
                .description("")
                .name(UUID.randomUUID().toString())
                .schemas(Lists.newArrayList())
                .tag("tag")
                .active(true)
                .build();
        assertTrue(cellSchemaDBCommand.save(cell).isPresent());
    }

    @Test
    void testGet() {
        val cell = CellSchema.builder()
                .description("")
                .name(UUID.randomUUID().toString())
                .schemas(Lists.newArrayList())
                .tag("tag")
                .active(true)
                .build();
        val savedCell = cellSchemaDBCommand.save(cell);
        assertTrue(savedCell.isPresent());
        val fetchedCell = cellSchemaDBCommand.getFromDb(savedCell.get().getUuid());
        assertTrue(fetchedCell.isPresent());
    }

    @Test
    void testGetAll() {
        val cell = CellSchema.builder()
                .description("")
                .name(UUID.randomUUID().toString())
                .schemas(Lists.newArrayList())
                .tag("tag")
                .active(true)
                .build();
        assertTrue(cellSchemaDBCommand.save(cell).isPresent());
        assertNotEquals(0, cellSchemaDBCommand.getAll());
    }

    @Test
    void testUpdate() {
        val cell = CellSchema.builder()
                .description("")
                .name(UUID.randomUUID().toString())
                .schemas(Lists.newArrayList())
                .tag("tag")
                .active(true)
                .build();
        val savedCell = cellSchemaDBCommand.save(cell);
        assertTrue(savedCell.isPresent());
        val updatedCell = savedCell.get();
        updatedCell.setActive(false);
        assertTrue(cellSchemaDBCommand.update(updatedCell).isPresent());
        val uCell = cellSchemaDBCommand.getFromDb(updatedCell.getUuid()).get();
        assertFalse(uCell.isActive());
    }
}
