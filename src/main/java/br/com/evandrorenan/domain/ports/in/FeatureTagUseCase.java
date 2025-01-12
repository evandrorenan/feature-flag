package br.com.evandrorenan.domain.ports.in;

import java.util.Map;

public interface FeatureTagUseCase {
    String X_FEATURE_FLAG_TAG = "x-feature-flag-tag";
    String BASELINE = "Baseline";

    Map<String, String> run(String body, Map<String, String> headers, ContextBuilder contextBuilder);
}
