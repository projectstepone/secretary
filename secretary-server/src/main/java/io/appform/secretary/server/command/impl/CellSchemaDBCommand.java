package io.appform.secretary.server.command.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.appform.dropwizard.sharding.dao.LookupDao;
import io.appform.secretary.model.schema.cell.CellSchema;
import io.appform.secretary.server.command.CellSchemaProvider;
import io.appform.secretary.server.dao.StoredCellSchema;
import io.appform.secretary.server.translator.data.CellSchemaTranslator;
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
public class CellSchemaDBCommand implements CellSchemaProvider {

    private final CellSchemaTranslator translator;
    private final LookupDao<StoredCellSchema> lookupDao;
    private final LoadingCache<String, Optional<CellSchema>> cache;

    @Inject
    public CellSchemaDBCommand(LookupDao<StoredCellSchema> lookupDao,
                               CellSchemaTranslator translator) {
        this.lookupDao = lookupDao;
        this.translator = translator;
        log.info("Initializing cache CELL_SCHEMA_CACHE");
        cache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(300, TimeUnit.SECONDS)
                .build(key -> {
                    log.debug("Loading data for schema with uuid: {}", key);
                    return getFromDb(key);
                });
    }

    @Override
    public Optional<CellSchema> save(CellSchema cellSchema) {
        try {
            cellSchema.setUuid(createUuid());
            val savedData = lookupDao.save(translator.toDao(cellSchema));
            savedData.ifPresent(data -> cache.refresh(data.getUuid()));
            return savedData.map(translator::toDto);
        } catch (Exception ex) {
            log.error("Failed to save cell schema {} : {}", cellSchema, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<CellSchema> update(CellSchema cellSchema) {
        try {
            boolean updated = lookupDao.update(cellSchema.getUuid(), storedSchema -> {
                if (storedSchema.isPresent()) {
                    storedSchema.get().setActive(cellSchema.isActive());
                    storedSchema.get().setValidators(translator.toDao(cellSchema).getValidators());
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
            log.error("Failed to update cell schema with uuid {} to {} : {}",
                    cellSchema.getUuid(), cellSchema, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<CellSchema> get(String uuid) {
        try {
            return cache.get(uuid);
        } catch (Exception ex) {
            log.warn("Unable to find schema with uuid: {}. Exception: {}", uuid, ex.getMessage());
            return Optional.empty();
        }
    }

    public Optional<CellSchema> getFromDb(String uuid) {
        try {
            val optional = lookupDao.get(uuid);
            return optional.map(translator::toDto);
        } catch (Exception ex) {
            log.warn("Unable to find schema with uuid: {}. Exception: {}", uuid, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<CellSchema> getAll() {
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(StoredCellSchema.class);
            return lookupDao.scatterGather(criteria)
                    .stream()
                    .map(translator::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Exception while fetching all cell schemas: {}", ex.getMessage());
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
