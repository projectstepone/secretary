package io.appform.secretary.utils;

import io.appform.secretary.dao.StoredWorkflow;
import io.appform.secretary.model.Workflow;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WorkflowUtils {

    public Workflow toDto(StoredWorkflow dao) {
        return Workflow.builder()
                .name(dao.getName())
                .enabled(dao.isEnabled())
                .build();
    }

    public StoredWorkflow toDao(Workflow dto) {
        return StoredWorkflow.builder()
                .name(dto.getName())
                .enabled(dto.isEnabled())
                .build();
    }
}
