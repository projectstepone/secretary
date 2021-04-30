package io.appform.secretary.server.command.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.appform.dropwizard.sharding.dao.LookupDao;
import io.appform.secretary.model.RawDataEntry;
import io.appform.secretary.server.command.FileRowDataProvider;
import io.appform.secretary.server.dao.StoredFileRowMetadata;
import io.appform.secretary.server.utils.RawDataUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class FileRowDataDBCommand implements FileRowDataProvider {

    private final LookupDao<StoredFileRowMetadata> lookupDao;
    private final LoadingCache<String, Optional<RawDataEntry>> cache;

    @Inject
    public FileRowDataDBCommand(LookupDao<StoredFileRowMetadata> lookupDao) {
        this.lookupDao = lookupDao;
        log.info("Initializing cache FILE_ROW_METADATA_CACHE");
        cache = Caffeine.newBuilder()
                .maximumSize(1_000_000)
                .expireAfterWrite(300, TimeUnit.SECONDS)
                .build(key -> {
                    log.debug("Loading data for key: {}", key);
                    return getFromDb(key);
                });
    }

    private Optional<RawDataEntry> getFromDb(String key) {
        try {
            val optional = lookupDao.get(key);
            return optional.map(RawDataUtils::toDto);
        } catch (Exception ex) {
            log.warn("Unable to find entry for key: {}. Exception: {}", key, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<RawDataEntry> get(String key) {
        try {
            return cache.get(key);
        } catch (Exception ex) {
            log.warn("Unable to find entry for key: {}. Exception: {}", key, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<RawDataEntry> getByFileId(String fileId) {
        try {
            return lookupDao.scatterGather(DetachedCriteria.forClass(StoredFileRowMetadata.class)
                    .add(Restrictions.eq("fileId", fileId)))
                    .stream()
                    .map(RawDataUtils::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.warn("Unable to find entries for fileId: {}. Exception: {}", fileId, ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<RawDataEntry> save(RawDataEntry entry) {
        try {
            val savedData = lookupDao.save(RawDataUtils.toDao(entry));
            savedData.ifPresent(data -> cache.refresh(data.getKey()));
            return savedData.map(RawDataUtils::toDto);
        } catch (Exception ex) {
            log.warn("Unable to save entry: {}. Exception: {}", entry, ex.getMessage());
            return Optional.empty();
        }
    }

}
