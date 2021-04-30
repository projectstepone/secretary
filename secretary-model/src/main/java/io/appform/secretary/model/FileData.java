package io.appform.secretary.model;

import io.appform.secretary.model.state.FileState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileData {
    private String uuid;
    private String name;
    private String user;
    private String workflow;
    private String hash;
    private long entryCount;
    private FileState state;
}
