package io.appform.secretary.server.executor;

import io.appform.secretary.server.internal.model.InputFileData;

public interface DataExecutor {

    void processFile(InputFileData data);
}
