package io.appform.secretary.server.command.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.appform.dropwizard.sharding.dao.LookupDao;
import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.server.command.ValidationSchemaProvider;
import io.appform.secretary.server.dao.StoredValidationSchema;
import io.appform.secretary.server.translator.data.SchemaTranslator;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.hibernate.criterion.DetachedCriteria;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class ValidationSchemaDBCommand implements ValidationSchemaProvider {

    private final LookupDao<StoredValidationSchema> lookupDao;
    private final LoadingCache<String, Optional<CellSchema>> cache;

    @Inject
    public ValidationSchemaDBCommand(LookupDao<StoredValidationSchema> lookupDao) {
        this.lookupDao = lookupDao;
        log.info("Initializing cache SCHEMA_CACHE");
        cache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(300, TimeUnit.SECONDS)
                .build(key -> {
                    log.debug("Loading data for file with uuid: {}", key);
                    return getFromDb(key);
                });
    }

    @Override
    public Optional<CellSchema> save(CellSchema cellSchema) {
        try {
            cellSchema.setUuid(createUuid());
            val savedData = lookupDao.save(SchemaTranslator.toDao(cellSchema));
            savedData.ifPresent(data -> cache.refresh(data.getUuid()));
            return savedData.map(SchemaTranslator::toDto);
        } catch (Exception ex) {
            log.error("Failed to save cellSchema {} : {}", cellSchema, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<CellSchema> update(CellSchema cellSchema) {
        try {
            boolean updated = lookupDao.update(cellSchema.getUuid(), storedSchema -> {
                if (storedSchema.isPresent()) {
                    storedSchema.get().setActive(cellSchema.isActive());
                    storedSchema.get().setValidators(SchemaTranslator.toDao(cellSchema).getValidators());
                }
                return storedSchema.orElse(null);
            });
            if (updated) {
                cache.refresh(cellSchema.getUuid());
                return getFromDb(cellSchema.getUuid());
            } else {
                return Optional.empty();
            }
        } catch (Exception ex) {
            log.error("Failed to update cellSchema for uuid {} to {} : {}",
                    cellSchema.getUuid(), cellSchema, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<CellSchema> get(String uuid) {
        try {
            return cache.get(uuid);
        } catch (Exception ex) {
            log.warn("Unable to find entry for uuid: {}. Exception: {}", uuid, ex.getMessage());
            return Optional.empty();
        }
    }

    public Optional<CellSchema> getFromDb(String uuid) {
        try {
            val optional = lookupDao.get(uuid);
            return optional.map(SchemaTranslator::toDto);
        } catch (Exception ex) {
            log.warn("Unable to find entry for uuid: {}. Exception: {}", uuid, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<CellSchema> getAll() {
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(StoredValidationSchema.class);
            return lookupDao.scatterGather(criteria)
                    .stream()
                    .map(SchemaTranslator::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Exception while fetching all schemas : {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private String createUuid() {
        var uuid = generateUuid();

        while (get(uuid).isPresent()) {
            uuid = generateUuid();
        }
        return uuid;
    }

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
