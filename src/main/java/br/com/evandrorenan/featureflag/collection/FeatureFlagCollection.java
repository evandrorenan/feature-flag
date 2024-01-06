/**
 * Interface representing a collection of feature flags.
 */
package br.com.evandrorenan.featureflag.collection;

public interface FeatureFlagCollection {
    /**
     * Check if a specific feature flag is enabled.
     *
     * @param id The identifier of the feature flag.
     * @return True if the feature flag is enabled, false otherwise.
     */
    boolean isEnabled(String id);

    /**
     * Execute the provided action if the feature flag is enabled,
     * otherwise execute a different action.
     *
     * @param id               The identifier of the feature flag.
     * @param actionIfEnabled  The action to be executed if the feature flag is enabled.
     * @param actionIfDisabled The action to be executed if the feature flag is disabled.
     */
    void ifEnabledOrElse(String id, Runnable actionIfEnabled,
                         Runnable actionIfDisabled);
}
