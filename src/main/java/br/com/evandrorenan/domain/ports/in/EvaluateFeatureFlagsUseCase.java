package br.com.evandrorenan.domain.ports.in;

import dev.openfeature.sdk.ImmutableContext;

import java.util.Map;

public interface EvaluateFeatureFlagsUseCase {
    <T> Map<String, T> evaluateAllFeatureFlags(ImmutableContext context, Class<T> type);
}
