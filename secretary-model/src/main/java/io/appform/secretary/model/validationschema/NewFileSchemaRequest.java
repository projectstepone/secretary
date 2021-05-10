package io.appform.secretary.model.validationschema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewFileSchemaRequest {

    @Valid
    private String workflow;

    @Valid
    @NotNull
    private List<String> schema;
}
