package io.appform.secretary.server.validator;

import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.server.command.FileDataProvider;
import io.appform.secretary.server.command.WorkflowProvider;
import io.appform.secretary.server.internal.model.InputFileData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileInputValidator implements Validator<InputFileData> {

    private final FileDataProvider fileDataDBCommand;
    private final WorkflowProvider workflowDBCommand;

    @Override
    public void validate(InputFileData input) {

        val workflowId = input.getWorkflow();
        val optionalWorkflow = workflowDBCommand.get(workflowId);
        if (!optionalWorkflow.isPresent()) {
            throw new SecretaryError("Workflow is not present: " + input.getWorkflow(),
                    ResponseCode.BAD_REQUEST);
        }
        if (!optionalWorkflow.get().isEnabled()) {
            throw new SecretaryError("Workflow is not enabled: " + input.getWorkflow(),
                    ResponseCode.BAD_REQUEST);
        }

        val data = fileDataDBCommand.getByHashValue(input.getHash());
        if(data.isPresent()) {
            input.setRetry(true);
            input.setUuid(data.get().getUuid());
            if (data.get().getState().isProcessed()) {
                throw new SecretaryError("File is already processed. File hash: " + input.getHash(),
                        ResponseCode.BAD_REQUEST);
            }
        }

    }

}
