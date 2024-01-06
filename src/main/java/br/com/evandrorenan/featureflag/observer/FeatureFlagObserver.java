/**
 * Interface representing a feature flag observer.
 */
package br.com.evandrorenan.featureflag.observer;

import br.com.evandrorenan.featureflag.subject.FeatureFlagSubject;

public interface FeatureFlagObserver {
    /**
     * Update method called when the state of the observed feature flag subject changes.
     *
     * @param subject The {@link FeatureFlagSubject} that triggered the update.
     */
    void update(FeatureFlagSubject subject);
}
