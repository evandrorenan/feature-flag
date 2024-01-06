/**
 * Class representing a feature flag.
 *
 * @param id          The identifier of the feature flag.
 * @param description The description of the feature flag.
 * @param isEnabled   True if the feature flag is enabled, false otherwise.
 * @param subject     The {@link FeatureFlagSubject} associated with the feature flag.
 */
package br.com.evandrorenan.featureflag;

import br.com.evandrorenan.featureflag.subject.FeatureFlagSubject;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FeatureFlag {
    private final String id;
    private final String description;
    private final boolean isEnabled;
    private final FeatureFlagSubject subject;
}
