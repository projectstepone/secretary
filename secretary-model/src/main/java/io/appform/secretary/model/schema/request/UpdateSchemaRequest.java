package io.appform.secretary.model.schema.request;

import io.appform.secretary.model.schema.ValidationSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSchemaRequest {

    @Valid
    private List<ValidationSchema> schema;

    @Valid
    @Builder.Default
    private boolean valid = true;
}
