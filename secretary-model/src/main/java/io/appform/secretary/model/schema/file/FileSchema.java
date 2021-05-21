package io.appform.secretary.model.schema.file;

import io.appform.secretary.model.Workflow;
import io.appform.secretary.model.schema.cell.CellSchema;
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
    private List<CellSchema> cellSchema;
}
