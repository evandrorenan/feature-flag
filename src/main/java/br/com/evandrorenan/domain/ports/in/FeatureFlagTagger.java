package br.com.evandrorenan.domain.ports.in;

import br.com.featureflagsdkjava.domain.model.Flag;

import java.util.Map;

public interface FeatureFlagTagger {
    String run(Flag flag, Map<String, String> context);
}
