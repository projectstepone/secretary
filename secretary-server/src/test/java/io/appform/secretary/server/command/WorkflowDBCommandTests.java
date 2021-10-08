package io.appform.secretary.server.command;

import com.google.inject.Inject;
import io.appform.secretary.model.Workflow;
import io.appform.secretary.server.TestBase;
import io.appform.secretary.server.command.impl.WorkflowDBCommand;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class WorkflowDBCommandTests extends TestBase {

    @Inject
    private WorkflowDBCommand workflowDBCommand;

    @Test
    void testSaveWorkflow() {
        val workflow = Workflow.builder()
                .name(UUID.randomUUID().toString())
                .enabled(true)
                .build();
        assertTrue(workflowDBCommand.save(workflow).isPresent());
    }

    @Test
    void testGetWorkflow() {
        val name = UUID.randomUUID().toString();
        val workflow = Workflow.builder()
                .name(name)
                .enabled(true)
                .build();
        assertTrue(workflowDBCommand.save(workflow).isPresent());
        assertTrue(workflowDBCommand.get(name).isPresent());
        assertTrue(workflowDBCommand.getFromDb(name).isPresent());
    }

    @Test
    void testGetAllWorkflow() {
        workflowDBCommand.save(Workflow.builder().name(UUID.randomUUID().toString()).build());
        workflowDBCommand.save(Workflow.builder().name(UUID.randomUUID().toString()).build());
        workflowDBCommand.save(Workflow.builder().name(UUID.randomUUID().toString()).build());
        workflowDBCommand.save(Workflow.builder().name(UUID.randomUUID().toString()).build());
        assertNotEquals(0, workflowDBCommand.getAll().size());
    }

    @Test
    void testUpdateWorkflow() {
        val name = UUID.randomUUID().toString();
        workflowDBCommand.save(Workflow.builder().name(name).build());
        workflowDBCommand.update(Workflow.builder().name(name).enabled(true).build());
        Optional<Workflow> optionalWorkflow = workflowDBCommand.getFromDb(name);
        assertTrue(optionalWorkflow.isPresent());
        assertTrue(optionalWorkflow.get().isEnabled());
    }
}
