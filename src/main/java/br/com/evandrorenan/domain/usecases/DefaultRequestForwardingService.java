package br.com.evandrorenan.domain.usecases;

import br.com.evandrorenan.domain.model.ProxyRequestContext;
import br.com.evandrorenan.domain.ports.RequestForwardingService;
import br.com.evandrorenan.domain.ports.out.RequestDetailsLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfeature.sdk.Client;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class DefaultRequestForwardingService implements RequestForwardingService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final Client featureFlagClient;
    private final RequestDetailsLogger requestDetailsLogger;

    @Autowired
    public DefaultRequestForwardingService(RestTemplate restTemplate, ObjectMapper mapper, Client featureFlagClient, RequestDetailsLogger requestDetailsLogger) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.featureFlagClient = featureFlagClient;
        this.requestDetailsLogger = requestDetailsLogger;
    }

    @Override
    public ResponseEntity<Object> forward(ProxyRequestContext proxyRequestContext) {
        requestDetailsLogger.log(proxyRequestContext);
        String destinationUrl = proxyRequestContext.resolveDestinationUrl(featureFlagClient, mapper);
        ResponseEntity<byte[]> response = sendRequestToDestination(destinationUrl, proxyRequestContext);

        return ResponseEntity.status(response.getStatusCode())
                             .headers(response.getHeaders())
                             .body(response.getBody());
    }

    private ResponseEntity<byte[]> sendRequestToDestination(String destinationUrl, ProxyRequestContext ctx) {
        String extraPath = ctx.getRequestPath().replaceFirst("^/v1/proxy/" + ctx.featureFlagName(), "");
        URI uri = buildURI(destinationUrl, ctx, extraPath);

        restTemplate.setErrorHandler(noOpErrorHandler());

        return restTemplate.exchange(
                uri,
                HttpMethod.valueOf(ctx.getHttpMethod()),
                new HttpEntity<>(ctx.getRequestBody(), ctx.getRequestHeaders()),
                byte[].class
        );
    }

    private static URI buildURI(String destinationUrl, ProxyRequestContext ctx, String extraPath) {
        MultiValueMap<String, String> requestParams = extractQueryParams(ctx, extraPath);

        extraPath = removeQueryParams(extraPath);

        return UriComponentsBuilder
                .fromUriString(destinationUrl)
                .path(extraPath)
                .queryParams(requestParams)
                .build(true)
                .toUri();
    }

    private static String removeQueryParams(String extraPath) {
        int qpPosition = extraPath.indexOf("?");

        if (qpPosition == -1) return extraPath;

        return extraPath.substring(0, qpPosition);
    }

    private static MultiValueMap<String, String> extractQueryParams(ProxyRequestContext ctx, String extraPath) {
        int qpPos = extraPath.indexOf('?');
        if (qpPos == -1) return toMultiValueMap(ctx.requestContext().parameters());

        String queryString = extraPath.substring(extraPath.indexOf('?') + 1);
        if (extraPath == null || extraPath.isEmpty()) {
            return toMultiValueMap(ctx.requestContext().parameters());
        }

        Map<String, String[]> params =
            Stream.of(queryString.split("&"))
              .map(param -> param.split("=", 2))
              .collect(Collectors.toMap(
                      keyValue -> keyValue[0],
                      keyValue -> keyValue.length > 1 ? keyValue[1].split(",") : new String[0],
                      (existing, replacement) -> replacement // Handle duplicate keys by taking the last value
              ));

        params.putAll(ctx.requestContext().parameters());

        return toMultiValueMap(params);
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
}