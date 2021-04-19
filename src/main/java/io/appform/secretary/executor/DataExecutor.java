package io.appform.secretary.executor;

import java.io.InputStream;

public interface DataExecutor {

    //TODO: Update workflow to enum
    void processFile(InputStream dataStream, String workflow);
}
