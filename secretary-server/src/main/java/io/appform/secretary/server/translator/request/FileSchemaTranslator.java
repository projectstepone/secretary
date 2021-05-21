package io.appform.secretary.server.translator.request;

import com.google.inject.Singleton;
import io.appform.secretary.model.Workflow;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.model.schema.file.FileSchema;
import io.appform.secretary.model.schema.file.request.CreateRequest;
import io.appform.secretary.server.command.CellSchemaProvider;
import io.appform.secretary.server.command.WorkflowProvider;
import lombok.RequiredArgsConstructor;
import lombok.val;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileSchemaTranslator {

    private final WorkflowProvider workflowProvider;
    private final CellSchemaProvider schemaProvider;

    public FileSchema translate(CreateRequest request) {
        val workflow = getWorkflow(request.getWorkflow());
        val schemas = getSchemas(request.getSchemas());

        return FileSchema.builder()
                .workflow(workflow)
                .cellSchema(schemas)
                .build();
    }

    private Workflow getWorkflow(String workflowId) {
        val workflow = workflowProvider.get(workflowId);
        if (!workflow.isPresent()) {
            throw new SecretaryError("Unable to find workflow: " + workflowId,
                    ResponseCode.BAD_REQUEST);
        }
        return workflow.get();
    }

    private List<CellSchema> getSchemas(List<String> schemaIds) {
        List<CellSchema> cellSchemas = schemaIds.stream()
                .map(schemaId -> {
                    val schema = schemaProvider.get(schemaId);
                    return schema.orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        val presentSchema = cellSchemas.stream()
                .map(CellSchema::getUuid)
                .collect(Collectors.toList());
        val missingSchema = schemaIds.stream()
                .filter(schema -> !presentSchema.contains(schema))
                .collect(Collectors.toList());

        if (cellSchemas.size() != schemaIds.size()) {
            throw new SecretaryError("Unable to find cellSchemas: " + missingSchema,
                    ResponseCode.BAD_REQUEST);
        }

        return cellSchemas;
    }
}
