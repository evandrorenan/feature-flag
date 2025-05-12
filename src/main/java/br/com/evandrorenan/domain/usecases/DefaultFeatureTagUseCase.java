package br.com.evandrorenan.domain.usecases;

import br.com.evandrorenan.domain.model.RequestContext;
import br.com.evandrorenan.domain.ports.in.EvaluateFeatureFlagsUseCase;
import br.com.evandrorenan.domain.ports.in.FeatureTagUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfeature.sdk.ImmutableContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Processor for setting feature flag tags in the exchange headers.
 */
@Slf4j
@Service
public class DefaultFeatureTagUseCase implements FeatureTagUseCase {
    private final EvaluateFeatureFlagsUseCase evaluateStringFlagsUseCase;
    private final ObjectMapper mapper;

    @Autowired
    public DefaultFeatureTagUseCase(
                EvaluateFeatureFlagsUseCase evaluateStringFlagsUseCase,
            ObjectMapper mapper) {
        this.evaluateStringFlagsUseCase = evaluateStringFlagsUseCase;
        this.mapper = mapper;
    }

    @Override
    public Map<String, String> run(HttpServletRequest request, HttpEntity<String> httpEntity) {

        RequestContext requestContext = RequestContext.from(request, httpEntity);
        Map<String, String> headers = new HashMap<>(requestContext.headers().asSingleValueMap());

        if (headers.containsKey(X_FEATURE_FLAG_TAG)) {
            log.info("Request already tagged: {}", headers.get(X_FEATURE_FLAG_TAG));
            return headers;
        }

        ImmutableContext featureFlagContext = requestContext.getFeatureFlagContext(mapper);
        Map<String, String> tags = evaluateStringFlagsUseCase.evaluateAllFeatureFlagsOfType(featureFlagContext, String.class);

        if (!shouldAddHeader(tags)) return headers;
        headers.put(X_FEATURE_FLAG_TAG, tags.values().toArray(new String[0])[0]);
        return headers;
    }

    protected static boolean shouldAddHeader(Map<String, String> tags) {
        if (tags.isEmpty()) {
            log.info("Request doesn't match any flag");
            return false;
        }

        if (tags.size() > 1) {
            log.warn("Request matches more than 1 flag. None applied.");
            return false;
        }

        String tag = tags.values().toArray(new String[0])[0];
        if (tag == null || tag.isEmpty()) {
            log.warn("Matched flag has no release id");
            return false;
        }

        return true;
    }
}