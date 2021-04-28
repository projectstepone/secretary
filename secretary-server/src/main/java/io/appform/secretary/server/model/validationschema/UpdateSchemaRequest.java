package io.appform.secretary.server.model.validationschema;

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
public class UpdateSchemaRequest {

    @Valid
    @NotBlank
    private String schema;
}
