package io.appform.secretary.server.clients.statesman;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.appform.http.client.HttpClient;
import io.appform.http.client.HttpClientBuilder;
import io.appform.http.client.HttpUtils;
import io.appform.http.client.StaticEndpointProvider;
import io.appform.http.client.models.Endpoint;
import io.appform.http.client.models.EndpointProviderContext;
import io.appform.http.client.models.HttpConfiguration;
import io.appform.secretary.model.statesman.CallbackRequest;
import io.appform.secretary.model.statesman.CallbackResponse;
import io.appform.secretary.server.utils.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.util.HashMap;

@Slf4j
@Singleton
public class StatesmanClient {

    private static final String RAW_CALLBACK_PATH = "callbacks/ingress/raw/%s";

    private final HttpClient httpClient;
    private final HttpConfiguration configuration;

    @Inject
    public StatesmanClient(
            final ObjectMapper objectMapper,
            @Named("statesmanHttpConfiguration") final HttpConfiguration configuration) {
        final OkHttpClient okHttpClient = HttpClientBuilder.builder()
                .withConfiguration(configuration)
                .build();
        this.configuration = configuration;
        this.httpClient = new HttpClient(objectMapper, okHttpClient);
    }

    public CallbackResponse rawCallback(final CallbackRequest request) {
        final Endpoint endpoint = new StaticEndpointProvider().provide(EndpointProviderContext.builder().configuration(this.configuration).build());
        final String baseUrl = resolveBaseUrl(endpoint);
        final String ivrProvider = "secretary";
        final String url = String.format("%s/%s", baseUrl, String.format(RAW_CALLBACK_PATH, ivrProvider));
        final Response response = httpClient.post(url, request, new HashMap<>());
        if (!response.isSuccessful()) {
            log.error("call to {} failed with code: {} res body: {}", url, response.code(), HttpUtils.bodyAsString(response));
            return CallbackResponse.builder()
                    .success(false)
                    .build();
        }
        log.info("call to {} completed with code: {}", url, response.code());
        return MapperUtils.deserialize(HttpUtils.bodyAsBytes(response), CallbackResponse.class);
    }

    private String resolveBaseUrl(final Endpoint endpoint) {
        final String schema = configuration.isSecure() ? "https" : "http";
        return String.format("%s://%s:%s", schema, endpoint.getHost(), endpoint.getPort());
    }
}
