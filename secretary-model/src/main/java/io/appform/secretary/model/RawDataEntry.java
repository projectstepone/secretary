package io.appform.secretary.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RawDataEntry {

    private String fileId;
    private long fileIndex;
    private int partitionId;
    //TODO: Check if we need an object instead of string
    private String data;
}
