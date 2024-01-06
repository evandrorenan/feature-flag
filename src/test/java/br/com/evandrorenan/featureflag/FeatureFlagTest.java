package br.com.evandrorenan.featureflag;

import br.com.evandrorenan.featureflag.subject.FeatureFlagSubject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Test class for {@link FeatureFlag}.
 */
class FeatureFlagTest {

    /**
     * Test the constructor and getters of the {@link FeatureFlag} class.
     */
    @Test
    void featureFlagConstructorAndGetters() {
        FeatureFlagSubject mockSubject = mock(FeatureFlagSubject.class);
        FeatureFlag featureFlag = FeatureFlag.builder()
                                             .id("testId")
                                             .description("Test Feature Flag")
                                             .isEnabled(true)
                                             .subject(mockSubject)
                                             .build();

        assertEquals("testId", featureFlag.getId());
        assertEquals("Test Feature Flag", featureFlag.getDescription());
        assertTrue(featureFlag.isEnabled());
        assertEquals(mockSubject, featureFlag.getSubject());
    }
}
