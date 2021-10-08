package io.appform.secretary.server.command.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.appform.dropwizard.sharding.dao.LookupDao;
import io.appform.secretary.model.FileData;
import io.appform.secretary.server.command.FileDataProvider;
import io.appform.secretary.server.dao.StoredFileData;
import io.appform.secretary.server.utils.FileDataUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class FileDataDBCommand implements FileDataProvider {

    private final LookupDao<StoredFileData> lookupDao;
    private final LoadingCache<String, Optional<FileData>> cache;

    @Inject
    public FileDataDBCommand(LookupDao<StoredFileData> fileDataLookupDao) {
        this.lookupDao = fileDataLookupDao;
        log.info("Initializing cache FILE_DATA_CACHE");
        cache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(300, TimeUnit.SECONDS)
                .build(key -> {
                    log.debug("Loading data for file with uuid: {}", key);
                    return getFromDb(key);
                });
    }

    @Override
    public Optional<FileData> save(FileData fileData) {
        try {
            fileData.setUuid(createUuid());
            val savedData = lookupDao.save(FileDataUtils.toDao(fileData));
            savedData.ifPresent(data -> cache.refresh(data.getUuid()));
            return savedData.map(FileDataUtils::toDto);
        } catch (Exception ex) {
            log.warn("Unable to save entry: {}. Exception: {}", fileData, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<FileData> update(FileData fileData) {
        try {
            val updated = lookupDao.update(fileData.getUuid(), entry -> {
                entry.ifPresent(file -> {
                    file.setState(fileData.getState().getValue());
                    file.setEntryCount(fileData.getCount());
                });
                return entry.orElse(null);
            });
            if (updated) {
                cache.refresh(fileData.getUuid());
                return getFromDb(fileData.getUuid());
            } else {
                return Optional.empty();
            }
        } catch (Exception ex) {
            log.warn("Unable to update entry: {}. Exception: {}", fileData, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<FileData> get(String uuid) {
        try {
            return cache.get(uuid);
        } catch (Exception ex) {
            log.warn("Unable to find entry for uuid: {}. Exception: {}", uuid, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<FileData> getAll() {
        try {
            return lookupDao.scatterGather(DetachedCriteria.forClass(StoredFileData.class))
                    .stream()
                    .sorted(Comparator.comparing(StoredFileData::getCreated).reversed())
                    .map(FileDataUtils::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.warn("Unable to get all entries. Exception: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<FileData> getFromDb(String uuid) {
        try {
            val optional = lookupDao.get(uuid);
            return optional.map(FileDataUtils::toDto);
        } catch (Exception ex) {
            log.warn("Unable to find entry for uuid: {}. Exception: {}", uuid, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<FileData> getByHashValue(String hashValue) {
        try {
            return lookupDao.scatterGather(
                    DetachedCriteria.forClass(StoredFileData.class)
                            .add(Restrictions.eq("hash", hashValue)))
                    .stream()
                    .sorted(Comparator.comparing(StoredFileData::getCreated).reversed())
                    .map(FileDataUtils::toDto)
                    .findFirst();
        } catch (Exception ex) {
            log.warn("Unable to find entry for hash value: {}. Exception: {}", hashValue, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<FileData> getByUser(String user) {
        try {
            return lookupDao.scatterGather(
                    DetachedCriteria.forClass(StoredFileData.class)
                            .add(Restrictions.eq("user", user)))
                    .stream()
                    .sorted(Comparator.comparing(StoredFileData::getCreated).reversed())
                    .map(FileDataUtils::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.warn("Unable to find entry for hash value: {}. Exception: {}", user, ex.getMessage());
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
