package io.appform.secretary.model.schema.cell.request;

import io.appform.secretary.model.schema.Schema;
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
public class CreateSchemaRequest {

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
    private List<Schema> schemas;
}
