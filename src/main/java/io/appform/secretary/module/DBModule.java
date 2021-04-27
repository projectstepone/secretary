package io.appform.secretary.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.appform.dropwizard.sharding.DBShardingBundle;
import io.appform.dropwizard.sharding.dao.LookupDao;
import io.appform.secretary.dao.StoredFileData;
import io.appform.secretary.dao.StoredValidationSchema;
import io.appform.secretary.dao.StoredWorkflow;

public class DBModule extends AbstractModule {

    private final DBShardingBundle<?> dbBundle;

    public DBModule(DBShardingBundle<?> dbBundle) {
        this.dbBundle = dbBundle;
    }

    @Singleton
    @Provides
    public LookupDao<StoredValidationSchema> providerStoredValidationSchemaLookupDao() {
        return dbBundle.createParentObjectDao(StoredValidationSchema.class);
    }

    @Singleton
    @Provides
    public LookupDao<StoredFileData> providerStoredFileDataLookupDao() {
        return dbBundle.createParentObjectDao(StoredFileData.class);
    }

    @Singleton
    @Provides
    public LookupDao<StoredWorkflow> providerStoredWorkflowLookupDao() {
        return dbBundle.createParentObjectDao(StoredWorkflow.class);
    }
}
