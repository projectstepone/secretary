package io.appform.secretary.command;

import io.appform.dropwizard.sharding.dao.LookupDao;
import io.appform.secretary.dao.StoredValidationSchema;
import io.appform.secretary.model.ValidationSchema;
import io.appform.secretary.utils.MapperUtils;
import io.appform.secretary.utils.ValidationSchemaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ValidationSchemaDBCommand implements ValidationSchemaProvider {

    private final LookupDao<StoredValidationSchema> schemaLookupDao;

    @Override
    public Optional<ValidationSchema> createSchema(ValidationSchema schema) {
        try {
            return schemaLookupDao.save(ValidationSchemaUtils.toDao(schema))
                    .map(ValidationSchemaUtils::toSchema);
        } catch (Exception ex) {
            log.error("Failed to save schema {} : {}", schema, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<ValidationSchema> updateSchema(ValidationSchema schema) {
        try {
            boolean updated = schemaLookupDao.update(schema.getUuid(), storedSchema -> {
                if (storedSchema.isPresent()) {
                    storedSchema.get().setActive(schema.isActive());
                    storedSchema.get().setSchema(MapperUtils.serialize(schema.getSchema()));
                }
                return storedSchema.orElse(null);
            });
            return updated ? getSchema(schema.getUuid()) : Optional.empty();
        } catch (Exception ex) {
            log.error("Failed to update schema for uuid {} to {} : {}",
                    schema.getUuid(), schema, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<ValidationSchema> getSchema(String uuid) {
        try {
            Optional<StoredValidationSchema> optional = schemaLookupDao.get(uuid);
            return optional.map(ValidationSchemaUtils::toSchema);
        } catch (Exception ex) {
            log.error("Exception while fetching schema {} : {}", uuid, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<ValidationSchema> getAllSchema() {
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(StoredValidationSchema.class);
            return schemaLookupDao.scatterGather(criteria)
                    .stream()
                    .map(ValidationSchemaUtils::toSchema)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Exception while fetching all schemas : {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<ValidationSchema> disableSchema(String uuid) {
        Optional<ValidationSchema> optional = getSchema(uuid);
        if (optional.isPresent()) {
            ValidationSchema schema = optional.get();
            schema.setActive(false);
            return updateSchema(schema);
        } else {
            log.warn("No schema found for entry : {}", uuid);
            return Optional.empty();
        }
    }
}
