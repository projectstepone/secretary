package io.appform.secretary.model.schema.cell.request;

import io.appform.secretary.model.schema.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSchemaRequest {

    @Valid
    @NotNull
    private List<Schema> schemas;

    @Valid
    @Builder.Default
    private boolean active = true;
}
