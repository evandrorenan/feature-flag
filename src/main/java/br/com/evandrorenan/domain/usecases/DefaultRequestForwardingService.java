package br.com.evandrorenan.domain.usecases;

import br.com.evandrorenan.domain.model.ProxyRequestContext;
import br.com.evandrorenan.domain.ports.RequestForwardingService;
import br.com.evandrorenan.domain.ports.in.EvaluateFlagsUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class DefaultRequestForwardingService implements RequestForwardingService {

    private final EvaluateFlagsUseCase evaluateStringFlagsUseCase;
    private final ObjectMapper mapper;

    @Autowired
    public DefaultRequestForwardingService(EvaluateFlagsUseCase evaluateStringFlagsUseCase, ObjectMapper mapper) {
        this.evaluateStringFlagsUseCase = evaluateStringFlagsUseCase;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<Object> forward(String encodedUrlParam, HttpServletRequest request, HttpEntity<String> httpEntity) {
        ProxyRequestContext proxyRequestContext = new ProxyRequestContext(encodedUrlParam, request, httpEntity);
        logRequestDetails(proxyRequestContext);
        String decodedDestinationUrl = resolveDestinationUrl(proxyRequestContext);
        ResponseEntity<byte[]> response = sendRequestToDestination(decodedDestinationUrl, proxyRequestContext);

        return ResponseEntity.status(response.getStatusCode())
                             .headers(response.getHeaders())
                             .body(response.getBody());
    }

    private static ResponseEntity<byte[]> sendRequestToDestination(String decodedDestinationUrl, ProxyRequestContext proxyRequestContext) {
        String extraPath = proxyRequestContext.getRequestPath().replaceFirst(
                "^/v1/proxy/" + proxyRequestContext.encodedUrlParam(), "");
        String fullUrl = decodedDestinationUrl + extraPath;

        HttpMethod method = HttpMethod.valueOf(proxyRequestContext.getHttpMethod());
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(
                fullUrl,
                method,
                new HttpEntity<>(proxyRequestContext.getRequestBody(), proxyRequestContext.getRequestHeaders()),
                byte[].class
        );
    }

    private String resolveDestinationUrl(ProxyRequestContext proxyRequestContext) {
        Set<String> flagsResults = evaluateRoutingFeatureFlags(proxyRequestContext);
        return flagsResults.stream()
                   .findFirst()
                   .orElse(proxyRequestContext.getDecodedUrlParam());
    }

    private Set<String> evaluateRoutingFeatureFlags(ProxyRequestContext proxyRequestContext) {
        Map<String, String> featureFlagContext = proxyRequestContext.requestContext().getRequestContextMap(mapper);
        return evaluateStringFlagsUseCase.run(featureFlagContext);
    }

    private static void logRequestDetails(ProxyRequestContext proxyRequestContext) {
        log.info("Proxy Request -> Method: {}, URI: {}", proxyRequestContext.getHttpMethod(), proxyRequestContext.getRequestPath());
        log.info("Headers: {}", proxyRequestContext.getRequestHeaders());
        log.info("Body: {}", proxyRequestContext.getRequestBody());
    }
}
