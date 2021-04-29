package io.appform.secretary.server.validator;

public interface Validator<T> {

    boolean isValid(T input);
}
