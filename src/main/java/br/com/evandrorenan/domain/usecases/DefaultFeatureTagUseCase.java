package br.com.evandrorenan.domain.usecases;

import br.com.evandrorenan.domain.model.RequestContext;
import br.com.evandrorenan.domain.ports.in.EvaluateFlagsUseCase;
import br.com.evandrorenan.domain.ports.in.FeatureTagUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Processor for setting feature flag tags in the exchange headers.
 */
@Slf4j
@Service
public class DefaultFeatureTagUseCase implements FeatureTagUseCase {
    private final EvaluateFlagsUseCase evaluateStringFlagsUseCase;
    private final ObjectMapper mapper;

    @Autowired
    public DefaultFeatureTagUseCase(
            @Qualifier("evaluateStringFlags") EvaluateFlagsUseCase evaluateStringFlagsUseCase,
            ObjectMapper mapper) {
        this.evaluateStringFlagsUseCase = evaluateStringFlagsUseCase;
        this.mapper = mapper;
    }

    @Override
    public Map<String, String> run(HttpServletRequest request, HttpEntity<String> httpEntity) {

        RequestContext requestContext = RequestContext.from(request, httpEntity);
        Map<String, String> headers = requestContext.headers().asSingleValueMap();

        if (headers.containsKey(X_FEATURE_FLAG_TAG)) {
            log.info("Request already tagged: {}", headers.get(X_FEATURE_FLAG_TAG));
            return headers;
        }

        Map<String, String> featureFlagContext = requestContext.getRequestContextMap(mapper);
        Set<String> tags = evaluateStringFlagsUseCase.run(featureFlagContext);

        if (!shouldAddHeader(tags)) return headers;
        headers.put(X_FEATURE_FLAG_TAG, new ArrayList<>(tags).get(0));
        return headers;
    }

    private static boolean shouldAddHeader(Set<String> tags) {
        if (tags.isEmpty()) {
            log.info("Request doesn't match any flag");
            return false;
        }

        if (tags.size() > 1) {
            log.warn("Request matches more than 1 flag. None applied.");
            return false;
        }

        String tag = new ArrayList<>(tags).get(0);
        if (tag == null || tag.isEmpty() || tag.equalsIgnoreCase(BASELINE)) {
            log.warn("Matched flag has no release id");
            return false;
        }

        return true;
    }
}