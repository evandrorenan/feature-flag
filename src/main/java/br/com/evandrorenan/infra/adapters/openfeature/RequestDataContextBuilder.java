package br.com.evandrorenan.infra.adapters.openfeature;

import br.com.evandrorenan.domain.ports.in.ContextBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RequestDataContextBuilder implements ContextBuilder {

    public static final String REQUEST_KEY = "request";
    private static final String ERROR_KEY = "error";
    private static final String ERROR_VALUE = "serialization_failed";
    private static final String HEADERS_KEY = "headers";
    private static final String BODY_KEY = "body";

    private final ObjectMapper mapper;
    @Autowired
    public RequestDataContextBuilder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Map<String, String> run(Map<String, String> headers, String body) {
        ObjectNode context = createContext(headers, body);
        return wrapContextForFlagEngine(context);
    }

    private ObjectNode createContext(Map<String, String> headers, String body) {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode requestData = createHeadersAndBodyNodes(headers, body);
        root.set(REQUEST_KEY, requestData);
        return root;
    }

    private ObjectNode createHeadersAndBodyNodes(Map<String, String> headers, String body) {
        ObjectNode context = mapper.createObjectNode();
        addHeaders(context, headers);
        addBody(context, body);
        return context;
    }

    private void addHeaders(ObjectNode context, Map<String, String> headers) {
        context.set(HEADERS_KEY, mapper.valueToTree(headers));
    }

    private void addBody(ObjectNode context, String body) {
        if (body == null) return;
        try {
            Object bodyValue = mapper.readValue(body, Map.class);
            context.set(BODY_KEY, mapper.valueToTree(bodyValue));
        } catch (JsonProcessingException e) {
            log.warn("Body is not a json string", e);
            context.put(BODY_KEY, body);
        }
    }

    private Map<String, String> wrapContextForFlagEngine(ObjectNode context) {
        try {
            String jsonString = mapper.writeValueAsString(context);
            return Map.of(REQUEST_KEY, jsonString);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize context", e);
            return Map.of(ERROR_KEY, ERROR_VALUE);
        }
    }
}