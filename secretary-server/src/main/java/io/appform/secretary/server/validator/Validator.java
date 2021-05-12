package io.appform.secretary.server.validator;

public interface Validator<T> {

    void validate(T input);
}
