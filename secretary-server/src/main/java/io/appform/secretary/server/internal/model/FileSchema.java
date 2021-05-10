package io.appform.secretary.server.internal.model;

import io.appform.secretary.model.Workflow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSchema {

    private Workflow workflow;
    private List<Schema> schema;
}
