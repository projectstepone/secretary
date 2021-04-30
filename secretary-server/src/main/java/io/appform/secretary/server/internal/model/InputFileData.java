package io.appform.secretary.server.internal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputFileData {

    private String file;
    private byte[] content;
    private String workflow;
    private String user;
    private String hash;
    private boolean retry;
}
