package io.appform.secretary.model.schema.cell;

import io.appform.secretary.model.schema.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CellSchema {
    private String uuid;
    private String name;
    private String description;
    private String tag;
    private boolean active;
    private List<Schema> schemas;
}
