package io.appform.secretary.model.exception;

import com.google.common.base.Strings;
import lombok.Getter;

import javax.validation.Valid;
import java.util.Objects;

public class SecretaryError extends RuntimeException {

    private static final String DEFAULT_RESPONSE = "Something went wrong";

    @Getter
    @Valid
    private final ResponseCode responseCode;

    public SecretaryError() {
        super(DEFAULT_RESPONSE);
        this.responseCode = ResponseCode.INTERNAL_SERVER_ERROR;
    }

    public SecretaryError(final ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public SecretaryError(final String message, final ResponseCode responseCode) {
        super(message);
        this.responseCode = responseCode;
    }

    public SecretaryError(final String message, final Throwable cause, final ResponseCode responseCode) {
        super(message, cause);
        this.responseCode = responseCode;
    }

    public static SecretaryError propagate(final Throwable e) {
        return propagate(null, e, ResponseCode.INTERNAL_SERVER_ERROR);
    }

    public static SecretaryError propagate(final Throwable e, final ResponseCode responseCode) {
        return propagate(null, e, responseCode);
    }

    public static SecretaryError propagate(final String message, final ResponseCode responseCode) {
        return propagate(message, new SecretaryError(message, responseCode), responseCode);
    }

    private static SecretaryError propagate(String message, final Throwable e, final ResponseCode responseCode) {
        if (Strings.isNullOrEmpty(message)) {
            message = Objects.isNull(e.getCause()) ? null : e.getCause().toString();
        }
        if (e instanceof SecretaryError) {
            return (SecretaryError) e;
        }
        return new SecretaryError(message, e, responseCode);
    }
}
