package br.com.evandrorenan.featureflag.collection;

import br.com.evandrorenan.featureflag.FeatureFlag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FeatureFlagsTest {

    @Test
    void testIsEnabled() {
        // Create a list of FeatureFlag objects
        List<FeatureFlag> flagList = Arrays.asList(
                FeatureFlag.builder().id("Flag1").isEnabled(true).build(),
                FeatureFlag.builder().id("Flag2").isEnabled(false).build(),
                FeatureFlag.builder().id("Flag3").isEnabled(true).build()
        );

        // Create a FeatureFlags record using the list
        FeatureFlags featureFlags = new FeatureFlags(flagList);

        assertTrue(featureFlags.isEnabled("Flag1")); // Flag1 is enabled
        assertFalse(featureFlags.isEnabled("Flag2")); // Flag2 is disabled
        assertTrue(featureFlags.isEnabled("Flag3")); // Flag3 is enabled
        assertFalse(featureFlags.isEnabled("NonExistentFlag")); // Non-existent flag is considered disabled
    }

    @Test
    void testIfEnabledOrElse() {
        // Create a list of FeatureFlag objects
        List<FeatureFlag> flagList = Arrays.asList(
                FeatureFlag.builder().id("Flag1").isEnabled(true).build(),
                FeatureFlag.builder().id("Flag2").isEnabled(false).build()
        );

        // Create a FeatureFlags record using the list
        FeatureFlags featureFlags = new FeatureFlags(flagList);

        // Test action for enabled flag
        StringBuilder enabledActionResult = new StringBuilder();
        featureFlags.ifEnabledOrElse("Flag1", () -> enabledActionResult.append("Enabled"), () -> enabledActionResult.append("Disabled"));
        assertEquals("Enabled", enabledActionResult.toString());

        // Test action for disabled flag
        StringBuilder disabledActionResult = new StringBuilder();
        featureFlags.ifEnabledOrElse("Flag2", () -> disabledActionResult.append("Enabled"), () -> disabledActionResult.append("Disabled"));
        assertEquals("Disabled", disabledActionResult.toString());

        // Test action for non-existent flag (considered disabled)
        StringBuilder nonExistentActionResult = new StringBuilder();
        featureFlags.ifEnabledOrElse("NonExistentFlag", () -> nonExistentActionResult.append("Enabled"), () -> nonExistentActionResult.append("Disabled"));
        assertEquals("Disabled", nonExistentActionResult.toString());
    }
}
