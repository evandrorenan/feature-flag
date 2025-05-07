package br.com.evandrorenan.domain.usecases;

import br.com.evandrorenan.domain.model.ProxyRequestContext;
import br.com.evandrorenan.domain.ports.RequestForwardingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.ImmutableContext;
import dev.openfeature.sdk.exceptions.InvalidContextError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Service
public class DefaultRequestForwardingService implements RequestForwardingService {

    private final ObjectMapper mapper;
    private final Client featureFlagClient;

    @Autowired
    public DefaultRequestForwardingService(ObjectMapper mapper, Client featureFlagClient) {
        this.mapper = mapper;
        this.featureFlagClient = featureFlagClient;
    }

    @Override
    public ResponseEntity<Object> forward(String featureFlagName, HttpServletRequest request, HttpEntity<String> httpEntity) {
        ProxyRequestContext proxyRequestContext = new ProxyRequestContext(featureFlagName, request, httpEntity);
        logRequestDetails(proxyRequestContext);
        String destinationUrl = resolveDestinationUrl(proxyRequestContext);
        ResponseEntity<byte[]> response = sendRequestToDestination(destinationUrl, proxyRequestContext);

        return ResponseEntity.status(response.getStatusCode())
                             .headers(response.getHeaders())
                             .body(response.getBody());
    }

    private static ResponseEntity<byte[]> sendRequestToDestination(String destinationUrl, ProxyRequestContext ctx) {
        String extraPath = ctx.getRequestPath().replaceFirst("^/v1/proxy/" + ctx.featureFlagName(), "");
        URI uri = buildURI(destinationUrl, ctx, extraPath);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(noOpErrorHandler());

        return restTemplate.exchange(
                uri,
                HttpMethod.valueOf(ctx.getHttpMethod()),
                new HttpEntity<>(ctx.getRequestBody(), ctx.getRequestHeaders()),
                byte[].class
        );
    }

    private static URI buildURI(String destinationUrl, ProxyRequestContext ctx, String extraPath) {
        return UriComponentsBuilder
                .fromUriString(destinationUrl)
                .path(extraPath)
                .queryParams(toMultiValueMap(ctx.requestContext().parameters()))
                .build(true)
                .toUri();
    }

    private static MultiValueMap<String, String> toMultiValueMap(Map<String, String[]> rawParams) {
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        rawParams.forEach((key, values) -> {
            if (values != null) result.addAll(key, Arrays.asList(values));
        });
        return result;
    }

    private static ResponseErrorHandler noOpErrorHandler() {
        return new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) { /* no-op */ }
        };
    }

    private String resolveDestinationUrl(ProxyRequestContext proxyRequestContext) {

        ImmutableContext context = proxyRequestContext.requestContext().getFeatureFlagContext(mapper);
        String destinationUrl = this.featureFlagClient.getStringValue(
                proxyRequestContext.featureFlagName(), "", context);

        if (destinationUrl == null || destinationUrl.isEmpty()) {
            log.error("Flag {} evaluated but no destination was found. {}",
                    proxyRequestContext.featureFlagName(), context);
            throw new InvalidContextError("Feature Flag evaluation failed.");
        }

        return destinationUrl;
    }

    private static void logRequestDetails(ProxyRequestContext proxyRequestContext) {
        log.info("Proxy Request -> Method: {}, URI: {}", proxyRequestContext.getHttpMethod(), proxyRequestContext.getRequestPath());
        log.info("Headers: {}", proxyRequestContext.getRequestHeaders());
        log.info("Body: {}", proxyRequestContext.getRequestBody());
    }
}
