package io.appform.secretary.model.schema;

import io.appform.secretary.model.schema.impl.ListSchema;
import io.appform.secretary.model.schema.impl.RangeSchema;
import io.appform.secretary.model.schema.impl.RegexSchema;

public interface SchemaHandler<T>{

    public T handle(ListSchema schema);

    public T handle(RegexSchema schema);

    public T handle(RangeSchema schema);
}
