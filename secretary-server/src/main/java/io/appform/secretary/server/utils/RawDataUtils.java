package io.appform.secretary.server.utils;

import io.appform.secretary.model.RawDataEntry;
import io.appform.secretary.server.dao.StoredFileRowMetadata;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RawDataUtils {

    public RawDataEntry toDto(StoredFileRowMetadata dao) {
        return RawDataEntry.builder()
                .partitionId(dao.getPartitionId())
                .fileId(dao.getFileId())
                .fileIndex(dao.getFileIndex())
                .build();
    }

    public StoredFileRowMetadata toDao(RawDataEntry dto) {
        return StoredFileRowMetadata.builder()
                .fileId(dto.getFileId())
                .fileIndex(dto.getFileIndex())
                .key(CommonUtils.getKey(dto.getFileId(), dto.getFileIndex()))
                .partitionId(CommonUtils.getRawDataPartitionId(dto.getFileIndex()))
                .build();
    }
}
