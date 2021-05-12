package io.appform.secretary.model.schema.request;

import io.appform.secretary.model.schema.ValidationSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    private String description;

    @Valid
    @NotBlank
    private String tag;

    @Valid
    @NotNull
    private List<ValidationSchema> schema;
}
