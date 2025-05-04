package br.com.evandrorenan.domain.usecases;

import br.com.evandrorenan.domain.ports.in.EvaluateFlagsUseCase;
import br.com.evandrorenan.domain.ports.in.FeatureFlagTagger;
import br.com.featureflagsdkjava.domain.model.Flag;
import br.com.featureflagsdkjava.domain.ports.FeatureFlagQueryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@Service("evaluateStringFlags")
@Primary
public class EvaluateStringFlagsUseCase implements EvaluateFlagsUseCase {

    private final FeatureFlagQueryPort queryPort;
    private final FeatureFlagTagger evaluator;

    @Autowired
    public EvaluateStringFlagsUseCase(
            @Qualifier("databaseFeatureFlagQuery") FeatureFlagQueryPort queryPort,
            FeatureFlagTagger evaluator) {
        this.queryPort = queryPort;
        this.evaluator = evaluator;
    }

    @Override
    public Set<String> run(Map<String, String> featureFlagContext) {
        List<Flag> flags = queryPort.findFlagsByType(Flag.FlagType.STRING);

        Set<String> tags = new HashSet<>();
        flags.forEach(flag -> {
            String tag = this.evaluator.run(flag, featureFlagContext);
            if (tag == null || tag.isEmpty()) return;
            tags.add(tag);
        });
        return tags;
    }
}
