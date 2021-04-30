package io.appform.secretary.server.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Hex;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import java.security.MessageDigest;

@UtilityClass
public class CommonUtils {

    private static final int RAW_DATA_TOTAL_PARTITIONS = 16;
    private static final String SEPARATOR = ":";

    @SneakyThrows
    public String getHash(byte[] data) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(data);
        return Hex.encodeHexString(hashBytes);
    }

    public int getRawDataPartitionId(long index) {
        return (int) (index % RAW_DATA_TOTAL_PARTITIONS);
    }

    public String getKey(@Valid @NotBlank String id, long index) {
        return id + SEPARATOR + Long.toString(index);
    }

}
