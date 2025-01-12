package br.com.evandrorenan.domain.ports.in;

import br.com.featureflagsdkjava.domain.model.Flag;

public interface FeatureFlagPersistencePort {

    Flag save(Flag flag);
}
