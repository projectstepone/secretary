package io.appform.secretary.server.validator;

import com.google.inject.Singleton;
import io.appform.secretary.model.validationschema.NewSchemaRequest;
import io.appform.secretary.server.command.ValidationSchemaProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SchemaValidator implements Validator<NewSchemaRequest>{

    private final ValidationSchemaProvider schemaProvider;

    @Override
    public boolean isValid(NewSchemaRequest input) {
        // Add validation checks
        return true;
    }
}
