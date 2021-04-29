package io.appform.secretary.model.validationschema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewSchemaRequest {

    @Valid
    @NotBlank
    private String name;

    @Valid
    @NotBlank
    private String schema;
}
