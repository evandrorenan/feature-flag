package br.com.evandrorenan.domain.ports.in;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;

import java.util.Map;

public interface FeatureTagUseCase {
    String X_FEATURE_FLAG_TAG = "x-feature-flag-tag";
    String BASELINE = "Baseline";

    Map<String, String> run(HttpServletRequest request, HttpEntity<String> httpEntity);
}
