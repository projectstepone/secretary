package io.appform.secretary.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RawDataEntry {
    private String fileId;
    private long fileIndex;
    private int partitionId;
    private Map<String, String> data;
}
