package io.appform.secretary.server.executor;

import io.appform.secretary.server.model.InputFileData;

public interface DataExecutor {

    void processFile(InputFileData data);
}
