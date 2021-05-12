package io.appform.secretary.server.translator.request;

import com.google.inject.Singleton;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.model.schema.file.request.CreateSchemaRequest;
import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.server.command.ValidationSchemaProvider;
import io.appform.secretary.server.command.WorkflowProvider;
import io.appform.secretary.model.schema.file.FileSchema;
import lombok.RequiredArgsConstructor;
import lombok.val;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileSchemaRequestTranslator {

    private final WorkflowProvider workflowProvider;
    private final ValidationSchemaProvider schemaProvider;

    public FileSchema getFileSchema(CreateSchemaRequest request) {
        val workflow = workflowProvider.get(request.getWorkflow());
        if (!workflow.isPresent()) {
            throw new SecretaryError("Unable to find workflow: " + request.getWorkflow(),
                    ResponseCode.BAD_REQUEST);
        }

        List<CellSchema> cellSchemas = request.getSchemas().stream()
                .map(schemaId -> {
                    val schema = schemaProvider.get(schemaId);
                    return schema.orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        val presentSchema = cellSchemas.stream()
                .map(CellSchema::getUuid)
                .collect(Collectors.toList());
        val missingSchema = request.getSchemas().stream()
                .filter(schema -> !presentSchema.contains(schema))
                .collect(Collectors.toList());

        if (cellSchemas.size() != request.getSchemas().size()) {


            throw new SecretaryError("Unable to find cellSchemas: " + missingSchema,
                    ResponseCode.BAD_REQUEST);
        }

        return FileSchema.builder()
                .workflow(workflow.get())
                .cellSchema(cellSchemas)
                .build();
    }
}
