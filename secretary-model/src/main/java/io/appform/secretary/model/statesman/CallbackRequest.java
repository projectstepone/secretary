package io.appform.secretary.model.statesman;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CallbackRequest {
    private String id;
    private String apiPath;
    private Map<String, String> body;
}
