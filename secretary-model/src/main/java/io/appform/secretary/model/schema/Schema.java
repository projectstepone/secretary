package io.appform.secretary.model.schema;

import io.appform.secretary.model.schema.ValidationSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schema {

    private String uuid;
    private String name;
    private String description;
    private String tag;
    private boolean active;
    private List<ValidationSchema> schemas;

}
