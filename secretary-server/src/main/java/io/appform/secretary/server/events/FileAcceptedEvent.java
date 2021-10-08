package io.appform.secretary.server.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileAcceptedEvent {
    private String workflow;
    private int rowCounts;
}
