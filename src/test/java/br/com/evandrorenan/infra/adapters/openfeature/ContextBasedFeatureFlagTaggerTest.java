package br.com.evandrorenan.infra.adapters.openfeature;

import br.com.featureflagsdkjava.domain.model.Flag;
import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.MutableContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class ContextBasedFeatureFlagTaggerTest {

    @Mock
    private Client mockClient;

    private ContextBasedFeatureFlagTagger featureFlagTagger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        featureFlagTagger = new ContextBasedFeatureFlagTagger(mockClient);
    }

    @Test
    void shouldEvaluateFlagAndReturnTag() {
        // Arrange
        Flag flag = new Flag();
        flag.setFlagName("test-flag");
        Map<String, String> context = new HashMap<>();
        context.put("key1", "value1");
        context.put("key2", "value2");
        when(mockClient.getStringValue(eq("test-flag"), eq(""), any(MutableContext.class)))
                .thenReturn("tag-value");

        // Act
        String result = featureFlagTagger.run(flag, context);

        // Assert
        assertEquals("tag-value", result);
    }

    @Test
    void shouldReturnNullWhenTagIsEmpty() {
        // Arrange
        Flag flag = new Flag();
        flag.setFlagName("test-flag");
        Map<String, String> context = new HashMap<>();
        when(mockClient.getStringValue(eq("test-flag"), eq(""), any(MutableContext.class)))
                .thenReturn("");

        // Act
        String result = featureFlagTagger.run(flag, context);

        // Assert
        assertNull(result);
        verify(mockClient).getStringValue(eq("test-flag"), eq(""), any(MutableContext.class));
    }

    @Test
    void shouldReturnNullWhenTagIsNull() {
        // Arrange
        Flag flag = new Flag();
        flag.setFlagName("test-flag");
        Map<String, String> context = new HashMap<>();
        when(mockClient.getStringValue(eq("test-flag"), eq(""), any(MutableContext.class)))
                .thenReturn(null);

        // Act
        String result = featureFlagTagger.run(flag, context);

        // Assert
        assertNull(result);
        verify(mockClient).getStringValue(eq("test-flag"), eq(""), any(MutableContext.class));
    }
}
