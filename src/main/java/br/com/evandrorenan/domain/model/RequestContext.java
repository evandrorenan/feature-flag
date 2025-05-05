package br.com.evandrorenan.domain.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Builder
public record RequestContext(
        String method, String URI, org.springframework.http.HttpHeaders headers, byte[] body
) {
    public static RequestContext from(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return RequestContext.builder()
                .method(request.getMethod())
                .URI(request.getRequestURI())
                .headers(httpEntity.getHeaders())
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
    public Map<String, String> getRequestContextMap(ObjectMapper mapper) {
        try {
            String json = mapper.writeValueAsString(this);
            return mapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException occurred when trying to map requestContext: ", e);
            return Collections.emptyMap();
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
