package io.appform.secretary.model.schema;

import io.appform.secretary.model.schema.impl.ListValidationSchema;
import io.appform.secretary.model.schema.impl.RangeValidationSchema;
import io.appform.secretary.model.schema.impl.RegexValidationSchema;

public interface SchemaHandler<T>{

    public T handle(ListValidationSchema schema);

    public T handle(RegexValidationSchema schema);

    public T handle(RangeValidationSchema schema);
}
