package io.appform.secretary.server.actors.messages;

import io.appform.secretary.model.RawDataEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawDataEntryMessage {
    private RawDataEntry rawDataEntry;
}
