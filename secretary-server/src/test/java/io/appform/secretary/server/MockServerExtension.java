package io.appform.secretary.server;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

@Slf4j
public class MockServerExtension implements BeforeAllCallback, AfterEachCallback, AfterAllCallback, ParameterResolver {

    private static WireMockConfiguration wireMockConfiguration = new WireMockConfiguration().port(Options.DYNAMIC_PORT);
    private static WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration);

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        val namespace = ExtensionContext.Namespace.create(MockServerExtension.class);
        val store = extensionContext.getRoot().getStore(namespace);
        if (!wireMockServer.isRunning()) {
            wireMockServer.start();
        }
        store.getOrComputeIfAbsent(WireMockServer.class, key -> wireMockServer);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        val namespace = ExtensionContext.Namespace.create(MockServerExtension.class);
        val store = extensionContext.getRoot().getStore(namespace);
        store.get(WireMockServer.class, WireMockServer.class).stop();
        store.remove(WireMockServer.class);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        val type = parameterContext.getParameter().getType();
        return type == WireMockServer.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        val namespace = ExtensionContext.Namespace.create(MockServerExtension.class);
        val store = extensionContext.getStore(namespace);
        return store.get(parameterContext.getParameter().getType());
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        wireMockServer.resetRequests();
    }
}
