package br.com.evandrorenan.infra.adapters.openfeature;

import br.com.evandrorenan.domain.ports.in.ContextBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Processor for building the OpenFeature context from the request body.
 */
@Slf4j
@Component
class OpenFeatureContextBuilder implements ContextBuilder {
    public static final String REQUEST_BODY_AS_TEXT = "Texto da requisicao";
    private final ObjectMapper mapper;
    private final TypeReference<Map<String, Object>> typeReference;

    @Autowired
    OpenFeatureContextBuilder(ObjectMapper mapper, TypeReference<Map<String, Object>> typeReference) {
        this.mapper = mapper;
        this.typeReference = typeReference;
    }

    @Override
    public Map<String, String> run(String body, Map<String, String> headers) {
        Map<String, String> oFContext = new HashMap<>(headers);

        if (body == null) {
            log.info("Request has no request body");
            return oFContext;
        }

        if (!isJsonRequest(body)) {
            log.info("Request body is not a JSON string");
            oFContext.put(REQUEST_BODY_AS_TEXT, body);
            return oFContext;
        }

        return buildOpenFeatureContext(oFContext, body);
    }

    /**
     * Checks if the request body is a JSON string.
     *
     * @param body the request body
     * @return true if the body is a JSON string, false otherwise
     */
    private boolean isJsonRequest(String body) {
        body = body.replace("\n", "")
            .replace("\r", "")
            .replace("\t", "")
            .trim();

        return body.startsWith("{") && body.endsWith("}")
            || body.startsWith("[") && body.endsWith("]");
    }

    private Map<String, String> buildOpenFeatureContext(Map<String, String> oFContext, String requestBody) {
        try {
            Map<String, Object> requestMap = mapper.readValue(requestBody, typeReference);
            requestMap.forEach((k, v) -> oFContext.put(k, v.toString()));
        } catch (JsonProcessingException e) {
            log.warn("Error parsing JSON", e);
            log.warn("Content: {}", requestBody);
        }
        return oFContext;
    }
}