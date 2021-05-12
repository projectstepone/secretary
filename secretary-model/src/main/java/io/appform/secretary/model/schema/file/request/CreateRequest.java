package io.appform.secretary.model.schema.file.request;

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
@AllArgsConstructor
@NoArgsConstructor
public class CreateRequest {

    @Valid
    @NotBlank
    private String workflow;

    @Valid
    @NotNull
    private List<String> schemas;
}
