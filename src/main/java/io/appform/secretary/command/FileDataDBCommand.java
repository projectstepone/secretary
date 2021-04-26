package io.appform.secretary.command;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.appform.dropwizard.sharding.dao.LookupDao;
import io.appform.secretary.dao.StoredFileData;
import io.appform.secretary.model.FileData;
import io.appform.secretary.utils.FileDataUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
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
                .refreshAfterWrite(60, TimeUnit.SECONDS)
                .build(key -> {
                    log.debug("Loading data for file for key: {}", key);
                    return getFromDb(key);
                });
    }


    @Override
    public void save(FileData fileData) {
        try {
            String uuid = createUuid();
            fileData.setUuid(uuid);
            lookupDao.save(FileDataUtils.toDao(fileData));
        } catch (Exception ex) {
            log.warn("Unable to save entry: {}. Exception: {}", fileData, ex.getMessage());
        }
    }

    @Override
    public Optional<FileData> update(FileData fileData) {
        try {
            boolean updated = lookupDao.update(fileData.getUuid(), entry -> {
                entry.ifPresent(file -> file.setProcessed(fileData.isProcessed()));
                return entry.orElse(null);
            });
            return updated ? getFromDb(fileData.getUuid()): Optional.empty();
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
                    .map(FileDataUtils::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.warn("Unable to get all entries. Exception: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<FileData> getFromDb(String uuid) {
        try {
            Optional<StoredFileData> optional = lookupDao.get(uuid);
            return optional.map(FileDataUtils::toDto);
        } catch (Exception ex) {
            log.warn("Unable to find entry for uuid: {}. Exception: {}", uuid, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<FileData> getByHashValue(String hashValue) {
        try {
            return lookupDao.scatterGather(
                    DetachedCriteria.forClass(StoredFileData.class)
                            .add(Restrictions.eq("hash", hashValue)))
                    .stream()
                    .map(FileDataUtils::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.warn("Unable to find entry for hash value: {}. Exception: {}", hashValue, ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<FileData> getByUser(String user) {
        try {
            return lookupDao.scatterGather(
                    DetachedCriteria.forClass(StoredFileData.class)
                            .add(Restrictions.eq("user", user)))
                    .stream()
                    .map(FileDataUtils::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.warn("Unable to find entry for hash value: {}. Exception: {}", user, ex.getMessage());
            return Collections.emptyList();
        }
    }

    private String createUuid() {
        String uuid = generateUuid();

        while (get(uuid).isPresent()) {
            uuid = generateUuid();
        }
        return uuid;
    }

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
