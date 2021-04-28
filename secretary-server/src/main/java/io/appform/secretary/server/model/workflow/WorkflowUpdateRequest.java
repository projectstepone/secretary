package io.appform.secretary.server.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowUpdateRequest {

    @NotBlank
    private String workflow;
    private boolean enabled;
}
