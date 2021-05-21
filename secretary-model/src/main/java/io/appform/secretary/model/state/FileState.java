package io.appform.secretary.model.state;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum FileState {

    ACCEPTED(Values.ACCEPTED, false),
    PROCESSING(Values.PROCESSING, false),
    SKIPPED(Values.SKIPPED, false),
    PROCESSED(Values.PROCESSED, true);

    public static final class Values {
        public static final String ACCEPTED = "accepted";
        public static final String PROCESSING = "processing";
        public static final String SKIPPED = "skipped";
        public static final String PROCESSED = "processed";
    }

    @Getter
    private final String value;

    @Getter
    private final boolean processed;

    public static FileState get(String value) {

        return Arrays.stream(FileState.values())
                .filter(entry -> StringUtils.equals(value, entry.getValue()))
                .findFirst()
                .orElseGet(null);
    }

    FileState(String value, boolean processed) {
        this.value = value;
        this.processed = processed;
    }
}
