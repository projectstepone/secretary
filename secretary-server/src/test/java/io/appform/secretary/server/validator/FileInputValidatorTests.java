package io.appform.secretary.server.validator;

import com.google.inject.Inject;
import com.google.inject.Injector;
import io.appform.secretary.model.Workflow;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.server.TestBase;
import io.appform.secretary.server.command.FileDataProvider;
import io.appform.secretary.server.command.WorkflowProvider;
import io.appform.secretary.server.internal.model.InputFileData;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class FileInputValidatorTests extends TestBase {

    @Inject
    private FileDataProvider fileDataDBCommand;
    @Inject
    private WorkflowProvider workflowDBCommand;
    @Inject
    private FileInputValidator fileInputValidator;

    @Test
    void testInvalidWorkflow() {
        val workflow = UUID.randomUUID().toString();
        val inputFileData = InputFileData.builder()
                .workflow(workflow)
                .build();
        val error = assertThrows(SecretaryError.class, () -> fileInputValidator.validate(inputFileData));
        assertEquals(ResponseCode.BAD_REQUEST, error.getResponseCode());
    }

    @Test
    void testInactiveWorkflow() {
        val name = UUID.randomUUID().toString();
        val workflow = Workflow.builder()
                .name(name)
                .enabled(false)
                .build();
        workflowDBCommand.save(workflow);
        val inputFileData = InputFileData.builder()
                .workflow(name)
                .build();
        val error = assertThrows(SecretaryError.class, () -> fileInputValidator.validate(inputFileData));
        assertEquals(ResponseCode.BAD_REQUEST, error.getResponseCode());
    }
}
