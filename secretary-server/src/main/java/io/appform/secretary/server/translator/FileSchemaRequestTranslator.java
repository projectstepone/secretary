package io.appform.secretary.server.translator;

import com.google.inject.Singleton;
import io.appform.secretary.model.exception.ResponseCode;
import io.appform.secretary.model.exception.SecretaryError;
import io.appform.secretary.model.validationschema.NewFileSchemaRequest;
import io.appform.secretary.server.command.ValidationSchemaProvider;
import io.appform.secretary.server.command.WorkflowProvider;
import io.appform.secretary.server.internal.model.FileSchema;
import io.appform.secretary.server.internal.model.Schema;
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

    public FileSchema getFileSchema(NewFileSchemaRequest request) {
        val workflow = workflowProvider.get(request.getWorkflow());
        if (!workflow.isPresent()) {
            throw new SecretaryError("Unable to find workflow: " + request.getWorkflow(),
                    ResponseCode.BAD_REQUEST);
        }

        List<Schema> schemas = request.getSchema().stream()
                .map(schemaId -> {
                    val schema = schemaProvider.get(schemaId);
                    return schema.orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        val presentSchema = schemas.stream()
                .map(Schema::getUuid)
                .collect(Collectors.toList());
        val missingSchema = request.getSchema().stream()
                .filter(schema -> !presentSchema.contains(schema))
                .collect(Collectors.toList());

        if (schemas.size() != request.getSchema().size()) {


            throw new SecretaryError("Unable to find schemas: " + missingSchema,
                    ResponseCode.BAD_REQUEST);
        }

        return FileSchema.builder()
                .workflow(workflow.get())
                .schema(schemas)
                .build();
    }
}
