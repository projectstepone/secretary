package io.appform.secretary.model.state;

import lombok.Getter;

public enum FileState {

    UPLOADED(Values.UPLOADED, false),
    ACCEPTED(Values.ACCEPTED, false),
    PROCESSING(Values.PROCESSING, false),
    PROCESSED(Values.PROCESSED, true);

    public static final class Values {
        public static final String UPLOADED = "uploaded";
        public static final String ACCEPTED = "accepted";
        public static final String PROCESSING = "processing";
        public static final String PROCESSED = "processed";
    }

    @Getter
    private final String value;

    @Getter
    private final boolean processed;

    FileState(String value, boolean processed) {
        this.value = value;
        this.processed = processed;
    }
}
