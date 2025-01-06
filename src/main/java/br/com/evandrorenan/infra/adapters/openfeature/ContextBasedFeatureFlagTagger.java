package br.com.evandrorenan.infra.adapters.openfeature;

import br.com.evandrorenan.domain.ports.in.FeatureFlagTagger;
import br.com.featureflagsdkjava.domain.model.Flag;
import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.MutableContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ContextBasedFeatureFlagTagger implements FeatureFlagTagger {

    private final Client client;

    public ContextBasedFeatureFlagTagger(Client client) {
        this.client = client;
    }

    @Override
    public String run(Flag flag, Map<String, String> context) {
        MutableContext oFContext = new MutableContext();
        context.forEach(oFContext::add);
        return evaluateAndCategorizeFlag(flag, oFContext);
    }

    private String evaluateAndCategorizeFlag(Flag flag, MutableContext oFContext) {

        String tag = this.client.getStringValue(flag.getFlagName(), "", oFContext);
        if (tag == null || tag.isEmpty()) {
            log.info("Flag {} not evaluated", flag.getFlagName());
            return null;
        }

        log.info("Flag {} evaluated to {}", flag.getFlagName(), tag);
        return tag;
    }
}
