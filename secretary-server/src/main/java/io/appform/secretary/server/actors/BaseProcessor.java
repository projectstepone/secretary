package io.appform.secretary.server.actors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.dropwizard.actors.ConnectionRegistry;
import io.appform.dropwizard.actors.actor.Actor;
import io.appform.dropwizard.actors.actor.ActorConfig;
import io.appform.dropwizard.actors.exceptionhandler.ExceptionHandlingFactory;
import io.appform.dropwizard.actors.retry.RetryStrategyFactory;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public abstract class BaseProcessor<T> extends Actor<ActorType, T> {

    private Set<String> ignorableErrorCodes;

    @SuppressWarnings("java:S107")
    protected BaseProcessor(
            ActorType type,
            ActorConfig actorConfig,
            ConnectionRegistry registry,
            ExceptionHandlingFactory exceptionHandlingFactory,
            ObjectMapper mapper, Set<String> ignorableErrorCodes,
            Set<Class<?>> ignorableExceptions, Class<T> clazz) {
        super(type, actorConfig, registry, mapper, new RetryStrategyFactory(), exceptionHandlingFactory, clazz, ignorableExceptions);
        this.ignorableErrorCodes = ignorableErrorCodes;
    }

    @Override
    protected boolean isExceptionIgnorable(Throwable t) {
        if (ignorableErrorCodes != null && !ignorableErrorCodes.isEmpty()) {
            return false;
        }
        return super.isExceptionIgnorable(t);
    }
}
