package io.appform.secretary.server.command;

import com.google.inject.Inject;
import io.appform.secretary.model.Workflow;
import io.appform.secretary.model.schema.file.FileSchema;
import io.appform.secretary.server.TestBase;
import io.appform.secretary.server.command.impl.FileSchemaDBCommand;
import io.appform.secretary.server.command.impl.WorkflowDBCommand;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class FileSchemaDBCommandTests extends TestBase {

    @Inject
    private WorkflowDBCommand workflowDBCommand;
    @Inject
    private FileSchemaDBCommand fileSchemaDBCommand;

    @Test
    void testSave() {
        val name = UUID.randomUUID().toString();
        val workflow = Workflow.builder()
                .name(name)
                .build();
        workflowDBCommand.save(workflow);
        val fileSchema = FileSchema.builder()
                .workflow(workflow)
                .cellSchema(Lists.newArrayList())
                .build();
        assertTrue(fileSchemaDBCommand.save(fileSchema).isPresent());
    }

    @Test
    void testGetByWorkflow() {
        val name = UUID.randomUUID().toString();
        val workflow = Workflow.builder()
                .name(name)
                .enabled(true)
                .build();
        workflowDBCommand.save(workflow);
        val fileSchema = FileSchema.builder()
                .workflow(workflow)
                .cellSchema(Lists.newArrayList())
                .build();
        val optionsFileSchema = fileSchemaDBCommand.save(fileSchema);
        assertTrue(optionsFileSchema.isPresent());
        val savedFileSchema = optionsFileSchema.get();
        assertTrue(fileSchemaDBCommand.get(savedFileSchema.getWorkflow().getName()).isPresent());
    }
}
