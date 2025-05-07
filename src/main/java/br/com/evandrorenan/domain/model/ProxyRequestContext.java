package br.com.evandrorenan.domain.model;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
public record ProxyRequestContext (
    String featureFlagName,
    RequestContext requestContext) {

    public ProxyRequestContext(String encodedUrlParam, HttpServletRequest request, HttpEntity<String> httpEntity) {
        this(encodedUrlParam, RequestContext.from(request, httpEntity));
    }

    public String getDecodedUrlParam() {
        return new String(Base64.getUrlDecoder().decode(featureFlagName), StandardCharsets.UTF_8);
    }

    public String getHttpMethod() {
        return requestContext.method();
    }

    public String getRequestPath() {
        return requestContext.URI();
    }

    public HttpHeaders getRequestHeaders() {
        return requestContext.headers();
    }

    public Map<String, String> getRequestHeadersMap() {
        return requestContext.headers().asSingleValueMap();
    }

    public byte[] getRequestBody() {
        return requestContext.body();
    }
}