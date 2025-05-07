package br.com.evandrorenan.domain.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfeature.sdk.ImmutableContext;
import dev.openfeature.sdk.Structure;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Builder
public record RequestContext(
        String method, String URI, HttpHeaders headers, Map<String, String[]> parameters, byte[] body
) {
    public static RequestContext from(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return RequestContext.builder()
                .method(request.getMethod())
                .URI(request.getRequestURI())
                .headers(httpEntity.getHeaders())
                .parameters(request.getParameterMap())
                .body(getBodyAsBytes(request))
                .build();
    }

    private static byte[] getBodyAsBytes(HttpServletRequest request) {
        byte[] body = null;
        try {
            body = request.getInputStream().readAllBytes();
        } catch (IOException e) {
            log.info("Request has no body");
        }
        return body;
    }

    @SuppressWarnings("unchecked")
    public ImmutableContext getFeatureFlagContext(ObjectMapper mapper) {
        try {
            String asJson = mapper.writeValueAsString(this);
            Map<String, Object> asSimpleMap = mapper.readValue(asJson, Map.class);
            Structure flagStructure = Structure.mapToStructure(asSimpleMap);
            return new ImmutableContext(Thread.currentThread().getName(), flagStructure.asMap());
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException occurred when trying to build FeatureFlag context: ", e);
            return new ImmutableContext(Thread.currentThread().getName());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestContext requestContext = (RequestContext) o;
        return method.equals(requestContext.method)
            && URI.equals(requestContext.URI)
            && headers.equals(requestContext.headers)
            && Arrays.equals(body, requestContext.body);
    }

    @Override
    public String toString() {
        return "RequestContext[" +
                "method=" + method + ", " +
                "URI=" + URI + ", " +
                "headers=" + headers + ", " +
                "body=" + Arrays.toString(body) +
                "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, URI, headers, Arrays.hashCode(body));
    }
}
