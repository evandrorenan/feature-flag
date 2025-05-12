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
    public <T> Map<String, T> evaluateAllFeatureFlagsOfType(ImmutableContext context, Class<T> type) {
        List<Flag> flags = queryPort.findFlagsByType(getType(type));

        Map<String, T> results = new HashMap<>();
        flags.forEach(flag -> {
            T result = this.evaluateFeatureFlag(flag, context, type);
            if (result != null) results.put(flag.getName(), result);
        });
        return results;
    }


    private <T> T evaluateFeatureFlag(Flag flag, ImmutableContext context, Class<T> type) {
        Object value = switch (type.getSimpleName()) {
            case "String" -> client.getStringValue(flag.getName(), "", context);
            case "Boolean" -> client.getBooleanValue(flag.getName(), false, context);
            case "Integer" -> client.getIntegerValue(flag.getName(), 0, context);
            case "Double" -> client.getDoubleValue(flag.getName(), 0.0, context);
            default -> throw new IllegalArgumentException("Unsupported flag type: " + type);
        };
        return type.cast(value);
    }

    public static Flag.Type getType(Class<?> type) {
        return switch (type.getSimpleName()) {
            case "String" -> Flag.Type.STRING;
            case "Boolean" -> Flag.Type.BOOLEAN;
            case "Integer", "Double" -> Flag.Type.NUMBER;
            default -> Flag.Type.OBJECT;
        };
    }
}