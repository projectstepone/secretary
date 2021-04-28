package io.appform.secretary.server.validator;

import java.util.Optional;

public interface Validator<T> {

    public Optional<String> isValid(T input);
}
