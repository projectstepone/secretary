package io.appform.secretary.server.validator;

import io.appform.secretary.server.command.FileDataProvider;
import io.appform.secretary.server.command.WorkflowProvider;
import io.appform.secretary.server.model.InputFileData;
import io.appform.secretary.server.model.Workflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileInputValidator implements Validator<InputFileData> {

    private final FileDataProvider fileDataDBCommand;
    private final WorkflowProvider workflowDBCommand;

    @Override
    public Optional<String> isValid(InputFileData input) {

        String workflowId = input.getWorkflow();
        Optional<Workflow> optionalWorkflow = workflowDBCommand.get(workflowId);
        if (!optionalWorkflow.isPresent()) {
            return Optional.of("Workflow is not present: " + input.getWorkflow());
        }
        if (!optionalWorkflow.get().isEnabled()) {
            return Optional.of("Workflow is not enabled: " + input.getWorkflow());
        }

        //TODO: Handle file which isn't fully processed
        if(fileDataDBCommand.getByHashValue(input.getHash()).isPresent()) {
            return Optional.of("Duplicate file entry with hash: " + input.getHash());
        }

        return Optional.empty();
    }

}
