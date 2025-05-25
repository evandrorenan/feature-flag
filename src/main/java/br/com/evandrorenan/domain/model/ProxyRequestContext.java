package br.com.evandrorenan.domain.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.ImmutableContext;
import dev.openfeature.sdk.exceptions.InvalidContextError;
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

    public ProxyRequestContext(String featureFlagName, HttpServletRequest request, HttpEntity<String> httpEntity) {
        this(featureFlagName, RequestContext.from(request, httpEntity));
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

    public byte[] getRequestBody() {
        return requestContext.body();
    }

    public String resolveDestinationUrl(Client client, ObjectMapper mapper) {

        ImmutableContext context = this.requestContext().getFeatureFlagContext(mapper);
        String destinationUrl = client.getStringValue(
                this.featureFlagName(), "", context);

        if (destinationUrl == null || destinationUrl.isEmpty()) {
            log.error("Flag {} evaluated but no destination was found. {}",
                    this.featureFlagName(), context);
            throw new InvalidContextError("Feature Flag evaluation failed.");
        }

        return destinationUrl;
    }
}