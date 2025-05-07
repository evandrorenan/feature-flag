package br.com.evandrorenan.domain.usecases;

import br.com.evandrorenan.domain.ports.in.EvaluateFeatureFlagsUseCase;
import br.com.featureflagsdkjava.domain.model.Flag;
import br.com.featureflagsdkjava.domain.ports.FeatureFlagQueryPort;
import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.ImmutableContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class FeatureFlagEvaluator implements EvaluateFeatureFlagsUseCase {

    private final FeatureFlagQueryPort queryPort;
    private final Client client;

    @Autowired
    public FeatureFlagEvaluator(
            @Qualifier("databaseFeatureFlagQuery") FeatureFlagQueryPort queryPort,
            Client client) {
        this.queryPort = queryPort;
        this.client = client;
    }

    @Override
    public <T> Map<String, T> evaluateAllFeatureFlags(ImmutableContext context, Class<T> type) {
        List<Flag> flags = queryPort.findFlagsByType(getFlagType(type));

        Map<String, T> results = new HashMap<>();
        flags.forEach(flag -> {
            T result = this.evaluateFeatureFlag(flag, context, type);
            if (result != null) results.put(flag.getFlagName(), result);
        });
        return results;
    }


    @SuppressWarnings("unchecked")
    private <T> T evaluateFeatureFlag(Flag flag, ImmutableContext context, Class<T> type) {
        return switch (type.getSimpleName()) {
            case "String" -> (T) client.getStringValue(flag.getFlagName(), "", context);
            case "Boolean" -> (T) client.getBooleanValue(flag.getFlagName(), false, context);
            case "Integer" -> (T) client.getIntegerValue(flag.getFlagName(), 0, context);
            case "Double" -> (T) client.getDoubleValue(flag.getFlagName(), 0.0, context);
            default -> throw new IllegalArgumentException("Unsupported flag type: " + type);
        };
    }

    private static Flag.FlagType getFlagType(Class<?> type) {
        return switch (type.getSimpleName()) {
            case "String" -> Flag.FlagType.STRING;
            case "Boolean" -> Flag.FlagType.BOOLEAN;
            case "Integer", "Double" -> Flag.FlagType.NUMBER;
            default -> Flag.FlagType.OBJECT;
        };
    }
}
