/**
 * Default implementation of the {@link FeatureFlagSubject} interface.
 * Manages the state of a feature flag and notifies registered observers when the state changes.
 */
package br.com.evandrorenan.featureflag.subject;

import br.com.evandrorenan.featureflag.observer.FeatureFlagObserver;

import java.util.ArrayList;
import java.util.List;

public class DefaultFeatureFlagSubject implements FeatureFlagSubject {

    private boolean isEnabled;
    private final List<FeatureFlagObserver> observers = new ArrayList<>();

    /**
     * Check if the feature flag is currently enabled.
     *
     * @return True if the feature flag is enabled, false otherwise.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Enable the feature flag and notify observers of the state change.
     */
    public void enable() {
        isEnabled = true;
        notifyObservers();
    }

    /**
     * Disable the feature flag and notify observers of the state change.
     */
    public void disable() {
        isEnabled = false;
        notifyObservers();
    }

    /**
     * Add a new observer to be notified of state changes.
     *
     * @param observer The {@link FeatureFlagObserver} to be added.
     */
    @Override
    public void addObserver(FeatureFlagObserver observer) {
        observers.add(observer);
    }

    /**
     * Remove an observer from the list of notified observers.
     *
     * @param observer The {@link FeatureFlagObserver} to be removed.
     */
    @Override
    public void removeObserver(FeatureFlagObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notify all registered observers of the state change.
     */
    @Override
    public void notifyObservers() {
        for (FeatureFlagObserver observer : observers) {
            observer.update(this);
        }
    }
}
