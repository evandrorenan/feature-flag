package br.com.evandrorenan.domain.ports.in;

import java.util.Map;
import java.util.Set;

public interface EvaluateFlagsUseCase {
    Set<String> run(Map<String, String> featureFlagContext);
}
