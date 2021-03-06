package io.appform.secretary.server.command.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.inject.Singleton;
import io.appform.dropwizard.sharding.dao.LookupDao;
import io.appform.secretary.server.command.FileSchemaProvider;
import io.appform.secretary.server.dao.StoredFileSchema;
import io.appform.secretary.model.schema.file.FileSchema;
import io.appform.secretary.server.translator.data.FileSchemaTranslator;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hibernate.criterion.DetachedCriteria;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class FileSchemaDBCommand implements FileSchemaProvider {

    private final FileSchemaTranslator translator;
    private final LookupDao<StoredFileSchema> lookupDao;
    private final LoadingCache<String, Optional<FileSchema>> cache;

    @Inject
    public FileSchemaDBCommand(LookupDao<StoredFileSchema> lookupDao,
                               FileSchemaTranslator translator) {
        this.lookupDao = lookupDao;
        this.translator = translator;
        log.info("Initializing cache FILE_SCHEMA_CACHE");
        cache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(300, TimeUnit.SECONDS)
                .build(key -> {
                    log.debug("Loading data for workflow: {}", key);
                    return getFromDb(key);
                });
    }

    @Override
    public Optional<FileSchema> save(FileSchema schema) {
        try {
            val savedData = lookupDao.save(translator.toDao(schema));
            savedData.ifPresent(data -> cache.refresh(data.getWorkflow()));
            return savedData.map(translator::toDto);
        } catch (Exception ex) {
            log.error("Failed to save file schema {} : {}", schema, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<FileSchema> update(FileSchema schema) {
        try {
            boolean updated = lookupDao.update(schema.getWorkflow().getName(), storedSchema ->
            {
                storedSchema.ifPresent(storedFileSchema ->
                        storedFileSchema.setSchema(translator.toDao(schema).getSchema()));
                return storedSchema.orElse(null);
            });
            if (updated) {
                cache.refresh(schema.getWorkflow().getName());
                return getFromDb(schema.getWorkflow().getName());
            } else {
                return Optional.empty();
            }
        } catch (Exception ex) {
            log.error("Failed to update file schema for workflow {} to {} : {}",
                    schema.getWorkflow().getName(), schema, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<FileSchema> get(String workflow) {
        try {
            return cache.get(workflow);
        } catch (Exception ex) {
            log.warn("Unable to find file schema for workflow: {}. Exception: {}", workflow, ex.getMessage());
            return Optional.empty();
        }
    }

    public Optional<FileSchema> getFromDb(String workflow) {
        try {
            val optional = lookupDao.get(workflow);
            return optional.map(translator::toDto);
        } catch (Exception ex) {
            log.warn("Unable to find file schema for workflow: {}. Exception: {}", workflow, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<FileSchema> getAll() {
        try {
            DetachedCriteria criteria = DetachedCriteria.forClass(StoredFileSchema.class);
            return lookupDao.scatterGather(criteria)
                    .stream()
                    .map(translator::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Exception while fetching all file schemas: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

}
