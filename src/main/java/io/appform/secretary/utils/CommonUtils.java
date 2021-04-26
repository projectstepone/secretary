package io.appform.secretary.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;

@UtilityClass
public class CommonUtils {

    @SneakyThrows
    public String getHash(byte[] data) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(data);
        return Hex.encodeHexString(hashBytes);
    }
}
