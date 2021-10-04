package io.appform.secretary.server.actors.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.appform.dropwizard.actors.ConnectionRegistry;
import io.appform.dropwizard.actors.actor.ActorConfig;
import io.appform.dropwizard.actors.exceptionhandler.ExceptionHandlingFactory;
import io.appform.secretary.model.statesman.CallbackRequest;
import io.appform.secretary.model.statesman.CallbackResponse;
import io.appform.secretary.server.actors.ActorType;
import io.appform.secretary.server.actors.BaseProcessor;
import io.appform.secretary.server.actors.messages.RawDataEntryMessage;
import io.appform.secretary.server.clients.statesman.StatesmanClient;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Singleton
@EqualsAndHashCode(callSuper = true, exclude = {"statesmanClient"})
public class FileRowProcessor extends BaseProcessor<RawDataEntryMessage> {

    private final StatesmanClient statesmanClient;

    @Inject
    protected FileRowProcessor(
            @Named("actorConfigs") Map<ActorType, ActorConfig> actorConfigs,
            final ConnectionRegistry registry,
            final ObjectMapper mapper,
            final StatesmanClient statesmanClient) {
        super(
                ActorType.FILE_ROW_PROCESSOR,
                actorConfigs.get(ActorType.FILE_ROW_PROCESSOR),
                registry,
                new ExceptionHandlingFactory(),
                mapper,
                Collections.emptySet(),
                Collections.emptySet(),
                RawDataEntryMessage.class
        );
        this.statesmanClient = statesmanClient;
    }

    @Override
    protected boolean handle(RawDataEntryMessage rawDataEntryMessage) throws Exception {
        log.info("processing row: {}", rawDataEntryMessage);
        final CallbackResponse response = statesmanClient.rawCallback(CallbackRequest.builder()
                .apiPath("apiPath")
                .id("id")
                .body(rawDataEntryMessage.getRawDataEntry().getData())
                .build());
        return response.isSuccess();
    }
}
