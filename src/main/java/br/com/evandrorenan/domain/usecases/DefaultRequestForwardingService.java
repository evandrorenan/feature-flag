package br.com.evandrorenan.domain.usecases;

import br.com.evandrorenan.domain.ports.RequestForwardingService;
import br.com.evandrorenan.domain.ports.in.ContextBuilder;
import br.com.evandrorenan.domain.ports.in.EvaluateFlagsUseCase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class DefaultRequestForwardingService implements RequestForwardingService {

    private final ContextBuilder contextBuilder;
    private final EvaluateFlagsUseCase evaluateStringFlagsUseCase;

    @Autowired
    public DefaultRequestForwardingService(ContextBuilder contextBuilder, EvaluateFlagsUseCase evaluateStringFlagsUseCase) {
        this.contextBuilder = contextBuilder;
        this.evaluateStringFlagsUseCase = evaluateStringFlagsUseCase;
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
                "^/v1/proxy/" + proxyRequestContext.encodedUrlParam, "");
        String fullUrl = decodedDestinationUrl + extraPath;

        HttpMethod method = HttpMethod.valueOf(proxyRequestContext.getHttpMethod());
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(
                fullUrl,
                method,
                new HttpEntity<>(proxyRequestContext.getRequestBody(), proxyRequestContext.httpEntity.getHeaders()),
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
        Map<String, String> featureFlagContext =
            contextBuilder.run(proxyRequestContext.getRequestHeaders(), proxyRequestContext.getRequestBody());
        return evaluateStringFlagsUseCase.run(featureFlagContext);
    }

    private static void logRequestDetails(ProxyRequestContext proxyRequestContext) {
        log.info("Proxy Request -> Method: {}, URI: {}", proxyRequestContext.getHttpMethod(), proxyRequestContext.getRequestPath());
        log.info("Headers: {}", proxyRequestContext.getRequestHeaders());
        log.info("Body: {}", proxyRequestContext.getRequestBody());
    }

    static class ProxyRequestContext {
        private final String encodedUrlParam;
        private final HttpServletRequest request;
        private final HttpEntity<String> httpEntity;

        public ProxyRequestContext(String encodedUrlParam, HttpServletRequest request, HttpEntity<String> httpEntity) {
            this.encodedUrlParam = encodedUrlParam;
            this.request = request;
            this.httpEntity = httpEntity;
        }

        public String getDecodedUrlParam() {
            return new String(Base64.getUrlDecoder().decode(encodedUrlParam), StandardCharsets.UTF_8);
        }

        public String getHttpMethod() {
            return request.getMethod();
        }

        public String getRequestPath() {
            return request.getRequestURI();
        }

        public Map<String, String> getRequestHeaders() {
            return httpEntity.getHeaders().asSingleValueMap();
        }

        public String getRequestBody() {
            return httpEntity.getBody();
        }
    }
}
