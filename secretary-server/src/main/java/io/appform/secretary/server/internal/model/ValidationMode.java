package io.appform.secretary.server.internal.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum ValidationMode {
    NO_CHECK ("NO_CHECK") {
        public <T> T visit(ValidationModeVisitor<T> visitor) {
            return visitor.visitNoCheck();
        }
    },
    IN_LIST ("IN_LIST") {
        public <T> T visit(ValidationModeVisitor<T> visitor) {
            return visitor.visitInList();
        }
    },
    IN_RANGE_INT ("IN_RANGE_INT") {
        public <T> T visit(ValidationModeVisitor<T> visitor) {
            return visitor.visitInRangeInt();
        }
    },
    MATCH_REGEX ("MATCH_REGEX") {
        public <T> T visit(ValidationModeVisitor<T> visitor) {
            return visitor.visitMatchRegex();
        }
    };

    public abstract <T> T visit(ValidationModeVisitor<T> visitor);

    @Getter
    private String value;

    ValidationMode(String value) {
        this.value = value;
    }

    public static ValidationMode get(String value) {

        return Arrays.stream(ValidationMode.values())
                .filter(entry -> StringUtils.equals(value, entry.getValue()))
                .findFirst()
                .orElseGet(null);
    }

    public interface ValidationModeVisitor<T> {

        T visitNoCheck();

        T visitInList();

        T visitInRangeInt();

        T visitMatchRegex();
    }
}
