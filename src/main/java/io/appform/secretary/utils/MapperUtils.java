package io.appform.secretary.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

@Slf4j
@UtilityClass
public class MapperUtils {

    private static ObjectMapper objectMapper;

    public static void initialize(final ObjectMapper mapper) {
        objectMapper = mapper;
    }

    @SneakyThrows
    public static byte[] serialize(Object data) {
        return serialize(objectMapper, data);
    }

    @SneakyThrows
    public static <T> T deserialize( byte[] data, TypeReference<T> typeReference) {
        return deserialize(objectMapper, data, typeReference);
    }

    public static byte[] serialize(ObjectMapper mapper, Object data) throws Exception {
        try {
            if (data == null) {
                return new byte[0];
            }
            return mapper.writeValueAsBytes(data);
        } catch (Exception ex) {
            log.error("Error in serialization: {}", ex.getMessage());
            //TODO : Add proper exceptions
            throw new Exception("Something went wrong");
        }
    }

    @Nullable
    public static <T> T deserialize(ObjectMapper mapper, byte[] data, TypeReference<T> typeReference) throws Exception {
        try {
            if (data == null) {
                return null;
            }
            return mapper.readValue(data, typeReference);
        } catch (Exception ex) {
            log.error("Error in deserialization: {}", ex.getMessage());
            //TODO : Add proper exceptions
            throw new Exception("Something went wrong");
        }
    }
}
