package io.appform.secretary.server.validator;

import com.google.inject.Singleton;
import io.appform.secretary.model.schema.cell.request.CreateSchemaRequest;
import io.appform.secretary.server.command.ValidationSchemaProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SchemaValidator implements Validator<CreateSchemaRequest>{

    private final ValidationSchemaProvider schemaProvider;

    @Override
    public boolean isValid(CreateSchemaRequest input) {
        // Add validation checks
        return true;
    }
}
