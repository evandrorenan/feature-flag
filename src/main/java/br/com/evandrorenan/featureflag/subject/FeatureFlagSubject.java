/**
 * Interface representing a subject that observers can subscribe to for feature flag updates.
 */
package br.com.evandrorenan.featureflag.subject;

import br.com.evandrorenan.featureflag.observer.FeatureFlagObserver;

public interface FeatureFlagSubject {
    /**
     * Add a new observer to be notified of state changes.
     *
     * @param observer The {@link FeatureFlagObserver} to be added.
     */
    void addObserver(FeatureFlagObserver observer);

    /**
     * Remove an observer from the list of notified observers.
     *
     * @param observer The {@link FeatureFlagObserver} to be removed.
     */
    void removeObserver(FeatureFlagObserver observer);

    /**
     * Notify all registered observers of the state change.
     */
    void notifyObservers();
}
