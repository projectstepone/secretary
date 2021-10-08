package io.appform.secretary.server.executor;

import io.appform.secretary.model.FileData;
import io.appform.secretary.server.internal.model.InputFileData;

public interface DataExecutor {

    FileData processFile(InputFileData data);
}
