package io.appform.secretary.command;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.appform.dropwizard.sharding.dao.LookupDao;
import io.appform.secretary.dao.StoredWorkflow;
import io.appform.secretary.model.Workflow;
import io.appform.secretary.utils.WorkflowUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class WorkflowDBCommand implements WorkflowProvider{

    private final LookupDao<StoredWorkflow> lookupDao;
    private final LoadingCache<String, Optional<Workflow>> cache;

    @Inject
    public WorkflowDBCommand(LookupDao<StoredWorkflow> lookupDao) {
        this.lookupDao = lookupDao;
        log.info("Initializing cache WORKFLOW_CACHE");
        this.cache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(300, TimeUnit.SECONDS)
                .refreshAfterWrite(60, TimeUnit.SECONDS)
                .build(key -> {
                    log.debug("Loading data for file for key: {}", key);
                    return getFromDb(key);
                });
    }

    @Override
    public Optional<Workflow> save(Workflow workflow) {
        try {
            Optional<StoredWorkflow> savedWorkflow = lookupDao.save(WorkflowUtils.toDao(workflow));
            return savedWorkflow.map(WorkflowUtils::toDto);
        } catch (Exception ex) {
            log.warn("Unable to save entry: {}. Exception: {}", workflow, ex.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Workflow> getFromDb(String name) {
        try {
            Optional<StoredWorkflow> optional = lookupDao.get(name);
            return optional.map(WorkflowUtils::toDto);
        } catch (Exception ex) {
            log.warn("Unable to find entry for uuid: {}. Exception: {}", name, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Workflow> get(String name) {
        try {
            return cache.get(name);
        } catch (Exception ex) {
            log.warn("Unable to find entry for uuid: {}. Exception: {}", name, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Workflow> getAll() {
        try {
            return lookupDao.scatterGather(DetachedCriteria.forClass(StoredWorkflow.class))
                    .stream()
                    .map(WorkflowUtils::toDto)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.warn("Unable to get all entries. Exception: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Workflow> update(Workflow workflow) {
        try {
            boolean updated = lookupDao.update(workflow.getName(), entry -> {
                entry.ifPresent(storedWorkflow -> storedWorkflow.setEnabled(workflow.isEnabled()));
                return entry.orElse(null);
            });
            return updated ? getFromDb(workflow.getName()): Optional.empty();
        } catch (Exception ex) {
            log.warn("Unable to update entry: {}. Exception: {}", workflow, ex.getMessage());
            return Optional.empty();
        }
    }
}
