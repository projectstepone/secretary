package io.appform.secretary.server.translator.data;

import com.google.inject.Singleton;
import io.appform.secretary.model.Workflow;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.server.command.ValidationSchemaProvider;
import io.appform.secretary.server.command.WorkflowProvider;
import io.appform.secretary.server.dao.StoredFileSchema;
import io.appform.secretary.model.fileschema.FileSchema;
import io.appform.secretary.model.schema.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileSchemaTranslators {

    private final WorkflowProvider workflowProvider;
    private final ValidationSchemaProvider schemaProvider;

    private static final String SEPARATOR = " ";

    private Workflow getWorkflow(String workflowId) {
        val workflow = workflowProvider.get(workflowId);
        if (workflow.isPresent()) {
            return workflow.get();
        } else {
            throw new SecretaryError("Workflow is not present: " + workflowId,
                    ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    private List<Schema> getSchema(List<String> schemas) {
        return schemas.stream()
                .map(schemaId -> {
                    val schema = schemaProvider.get(schemaId);
                    if (schema.isPresent()) {
                        return schema.get();
                    } else {
                        throw new SecretaryError("Schema is not present: " + schemaId,
                                ResponseCode.INTERNAL_SERVER_ERROR);
                    }
                })
                .collect(Collectors.toList());
    }

    private String getString(List<String> data) {
        return String.join(SEPARATOR, data);
    }

    private List<String> getList(String input) {
        return Arrays.asList(input.split(SEPARATOR));
    }

    public FileSchema toDto(StoredFileSchema dao) {
        return FileSchema.builder()
                .workflow(getWorkflow(dao.getWorkflow()))
                .schema(getSchema(getList(dao.getSchema())))
                .build();
    }

    public StoredFileSchema toDao(FileSchema dto) {
        return StoredFileSchema.builder()
                .workflow(dto.getWorkflow().getName())
                .schema(getString(dto.getSchema().stream()
                        .map(Schema::getUuid)
                        .collect(Collectors.toList()))
                )
                .build();
    }
}
