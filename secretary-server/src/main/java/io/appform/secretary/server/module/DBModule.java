package io.appform.secretary.server.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.appform.dropwizard.sharding.DBShardingBundle;
import io.appform.dropwizard.sharding.dao.LookupDao;
import io.appform.secretary.server.dao.StoredFileData;
import io.appform.secretary.server.dao.StoredFileRowMetadata;
import io.appform.secretary.server.dao.StoredFileSchema;
import io.appform.secretary.server.dao.StoredCellSchema;
import io.appform.secretary.server.dao.StoredWorkflow;

public class DBModule extends AbstractModule {

    private final DBShardingBundle<?> dbBundle;

    public DBModule(DBShardingBundle<?> dbBundle) {
        this.dbBundle = dbBundle;
    }

    @Singleton
    @Provides
    public LookupDao<StoredCellSchema> providerStoredValidationSchemaLookupDao() {
        return dbBundle.createParentObjectDao(StoredCellSchema.class);
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

    @Singleton
    @Provides
    public LookupDao<StoredFileRowMetadata> providerStoredFileRowMetadataLookupDao() {
        return dbBundle.createParentObjectDao(StoredFileRowMetadata.class);
    }

    @Singleton
    @Provides
    public LookupDao<StoredFileSchema> providerStoredFileSchemaLookupDao() {
        return dbBundle.createParentObjectDao(StoredFileSchema.class);
    }
}
