package io.appform.secretary.model.exception;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public enum  ResponseCode {

    BAD_REQUEST(400, "BAD REQUEST"),
    NOT_FOUND(404, "NOT_FOUND"),
    JSON_ERROR(500, "JSON ERROR"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR");

    @Getter
    @NotNull
    private String message;

    @Getter
    @Min(400)
    @Max(599)
    private int code;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
