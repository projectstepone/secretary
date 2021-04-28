package io.appform.secretary.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationSchema {

    private String uuid;
    private String name;
    private int version;
    private boolean active;
    //TODO : Use a concrete implementation instead of object
    private Object schema;
}
