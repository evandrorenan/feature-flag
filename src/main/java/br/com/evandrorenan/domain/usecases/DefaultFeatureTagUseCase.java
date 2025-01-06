package br.com.evandrorenan.domain.usecases;

import br.com.evandrorenan.domain.ports.in.ContextBuilder;
import br.com.evandrorenan.domain.ports.in.FeatureFlagTagger;
import br.com.evandrorenan.domain.ports.in.FeatureTagUseCase;
import br.com.featureflagsdkjava.domain.model.Flag;
import br.com.featureflagsdkjava.domain.ports.FeatureFlagQueryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Processor for setting feature flag tags in the exchange headers.
 */
@Slf4j
@Component
public class DefaultFeatureTagUseCase implements FeatureTagUseCase {
    private final FeatureFlagQueryPort queryPort;
    private final FeatureFlagTagger evaluator;

    @Autowired
    public DefaultFeatureTagUseCase(@Qualifier("database") FeatureFlagQueryPort queryPort, FeatureFlagTagger evaluator) {
        this.queryPort = queryPort;
        this.evaluator = evaluator;
    }

    @Override
    public Map<String, String> run(String body, Map<String, String> headers, ContextBuilder contextBuilder) {
        if (headers.containsKey(X_FEATURE_FLAG_TAG)) {
            log.info("Request already tagged: {}", headers.get(X_FEATURE_FLAG_TAG));
            return headers;
        }

        Map<String, String> context = contextBuilder.run(body, headers);
        List<Flag> flags = queryPort.findFlagsByType(Flag.FlagType.STRING);

        Set<String> tags = new HashSet<>();
        flags.forEach(flag -> {
            String tag = this.evaluator.run(flag, context);
            if (tag == null || tag.isEmpty()) return;
            tags.add(tag);
        });

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