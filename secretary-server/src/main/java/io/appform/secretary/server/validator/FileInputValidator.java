package io.appform.secretary.server.validator;

import io.appform.secretary.model.FileData;
import io.appform.secretary.model.Workflow;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.server.command.FileDataProvider;
import io.appform.secretary.server.command.WorkflowProvider;
import io.appform.secretary.server.internal.model.InputFileData;
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
    public boolean isValid(InputFileData input) {

        String workflowId = input.getWorkflow();
        Optional<Workflow> optionalWorkflow = workflowDBCommand.get(workflowId);
        if (!optionalWorkflow.isPresent()) {
            throw new SecretaryError("Workflow is not present: " + input.getWorkflow(),
                    ResponseCode.BAD_REQUEST);
        }
        if (!optionalWorkflow.get().isEnabled()) {
            throw new SecretaryError("Workflow is not enabled: " + input.getWorkflow(),
                    ResponseCode.BAD_REQUEST);
        }

        //TODO: Handle file which isn't fully processed
        Optional<FileData> data = fileDataDBCommand.getByHashValue(input.getHash());
        if(data.isPresent()) {
            input.setRetry(true);
            if (data.get().getState().isProcessed()) {
                throw new SecretaryError("File is already processed. File hash: " + input.getHash(),
                        ResponseCode.BAD_REQUEST);
            }
        }

        return true;
    }

}
