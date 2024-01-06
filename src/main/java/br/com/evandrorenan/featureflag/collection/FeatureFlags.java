/**
 * Record representing a collection of feature flags.
 * Implements the {@link FeatureFlagCollection} interface.
 *
 * @param flags List of {@link FeatureFlag} objects.
 */
package br.com.evandrorenan.featureflag.collection;

import br.com.evandrorenan.featureflag.FeatureFlag;

import java.util.List;
import java.util.Optional;

public record FeatureFlags(List<FeatureFlag> flags) implements FeatureFlagCollection {

    /**
     * Check if a specific feature flag is enabled.
     *
     * @param id The identifier of the feature flag.
     * @return True if the feature flag is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled(String id) {
        Optional<FeatureFlag> firstFlag = this.flags.stream().filter(flag ->
                flag.getId().equalsIgnoreCase(id)).findFirst();

        return firstFlag.isPresent() && firstFlag.get().isEnabled();
    }

    /**
     * Execute the provided action if the feature flag is enabled,
     * otherwise execute a different action.
     *
     * @param id               The identifier of the feature flag.
     * @param actionIfEnabled  The action to be executed if the feature flag is enabled.
     * @param actionIfDisabled The action to be executed if the feature flag is disabled.
     */
    @Override
    public void ifEnabledOrElse(String id, Runnable actionIfEnabled,
                                Runnable actionIfDisabled) {
        if (this.isEnabled(id)) {
            actionIfEnabled.run();
            return;
        }
        actionIfDisabled.run();
    }
}
