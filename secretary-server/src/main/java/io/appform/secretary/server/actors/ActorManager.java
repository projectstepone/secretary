package io.appform.secretary.server.actors;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.appform.secretary.server.actors.messages.RawDataEntryMessage;
import io.appform.secretary.server.actors.processors.FileRowProcessor;
import io.dropwizard.lifecycle.Managed;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Singleton
public class ActorManager implements Managed {

    private final Injector injector;
    private final Map<ActorType, BaseProcessor<RawDataEntryMessage>> actors = Maps.newConcurrentMap();

    @Inject
    public ActorManager(final Injector injector) {
        this.injector = injector;
    }

    @Override
    public void start() throws Exception {
        actors.put(ActorType.FILE_ROW_PROCESSOR, injector.getInstance(FileRowProcessor.class));
        log.info("Actor started for: {}", ActorType.FILE_ROW_PROCESSOR.name());
    }

    @Override
    public void stop() throws Exception {

    }

    public void publish(ActorType actorType, RawDataEntryMessage message) {
        try {
            actors.get(actorType).publish(message);
        } catch (Exception e) {
            log.error("Error in publishing in rmq for message: {}", message, e);
        }
    }
}
