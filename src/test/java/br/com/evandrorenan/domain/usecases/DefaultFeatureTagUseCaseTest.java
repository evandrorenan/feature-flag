package br.com.evandrorenan.domain.usecases;

import br.com.evandrorenan.domain.ports.in.ContextBuilder;
import br.com.evandrorenan.domain.ports.in.FeatureFlagTagger;
import br.com.featureflagsdkjava.domain.model.Flag;
import br.com.featureflagsdkjava.domain.ports.FeatureFlagQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static br.com.evandrorenan.domain.ports.in.FeatureTagUseCase.BASELINE;
import static br.com.evandrorenan.domain.ports.in.FeatureTagUseCase.X_FEATURE_FLAG_TAG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultFeatureTagUseCaseTest {

    @Mock
    private FeatureFlagQueryPort queryPort;

    @Mock
    private FeatureFlagTagger evaluator;

    @Mock
    private ContextBuilder contextBuilder;

    private DefaultFeatureTagUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DefaultFeatureTagUseCase(queryPort, evaluator);
    }

    @Test
    void shouldReturnExistingHeadersWhenAlreadyTagged() {
        // Given
        String body = "test-body";
        Map<String, String> headers = new HashMap<>();
        headers.put(X_FEATURE_FLAG_TAG, "existing-tag");

        // When
        Map<String, String> result = useCase.run(body, headers, contextBuilder);

        // Then
        assertEquals(headers, result);
        verifyNoInteractions(queryPort, evaluator, contextBuilder);
    }

    @Test
    void shouldAddTagWhenSingleValidFlagMatches() {
        // Given
        String body = "test-body";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> context = new HashMap<>();
        List<Flag> flags = Collections.singletonList(new Flag());
        String expectedTag = "valid-tag";

        when(contextBuilder.run(body, headers)).thenReturn(context);
        when(queryPort.findFlagsByType(Flag.FlagType.STRING)).thenReturn(flags);
        when(evaluator.run(any(Flag.class), eq(context))).thenReturn(expectedTag);

        // When
        Map<String, String> result = useCase.run(body, headers, contextBuilder);

        // Then
        assertTrue(result.containsKey(X_FEATURE_FLAG_TAG));
        assertEquals(expectedTag, result.get(X_FEATURE_FLAG_TAG));
    }

    @Test
    void shouldNotAddTagWhenNoFlagsMatch() {
        // Given
        String body = "test-body";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> context = new HashMap<>();
        List<Flag> flags = Collections.singletonList(new Flag());

        when(contextBuilder.run(eq(body), eq(headers))).thenReturn(context);
        when(queryPort.findFlagsByType(Flag.FlagType.STRING)).thenReturn(flags);
        when(evaluator.run(any(Flag.class), eq(context))).thenReturn("");

        // When
        Map<String, String> result = useCase.run(body, headers, contextBuilder);

        // Then
        assertFalse(result.containsKey(X_FEATURE_FLAG_TAG));
    }

    @Test
    void shouldNotAddTagWhenMultipleFlagsMatch() {
        // Given
        String body = "test-body";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> context = new HashMap<>();
        Flag flag1 = new Flag();
        flag1.setFlagName("flag1");
        Flag flag2 = new Flag();
        flag2.setFlagName("flag2");
        List<Flag> flags = Arrays.asList(flag1, flag2);

        when(contextBuilder.run(eq(body), eq(headers))).thenReturn(context);
        when(queryPort.findFlagsByType(Flag.FlagType.STRING)).thenReturn(flags);
        when(evaluator.run(flag1, context)).thenReturn("tag1");
        when(evaluator.run(flag2, context)).thenReturn("tag2");

        // When
        Map<String, String> result = useCase.run(body, headers, contextBuilder);

        // Then
        assertFalse(result.containsKey(X_FEATURE_FLAG_TAG));
    }

    @Test
    void shouldNotAddTagWhenBaselineFlag() {
        // Given
        String body = "test-body";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> context = new HashMap<>();
        List<Flag> flags = Collections.singletonList(new Flag());

        when(contextBuilder.run(eq(body), eq(headers))).thenReturn(context);
        when(queryPort.findFlagsByType(Flag.FlagType.STRING)).thenReturn(flags);
        when(evaluator.run(any(Flag.class), eq(context))).thenReturn(BASELINE);

        // When
        Map<String, String> result = useCase.run(body, headers, contextBuilder);

        // Then
        assertFalse(result.containsKey(X_FEATURE_FLAG_TAG));
    }

    @Test
    void shouldHandleNullTagFromEvaluator() {
        // Given
        String body = "test-body";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> context = new HashMap<>();
        List<Flag> flags = Collections.singletonList(new Flag());

        when(contextBuilder.run(eq(body), eq(headers))).thenReturn(context);
        when(queryPort.findFlagsByType(Flag.FlagType.STRING)).thenReturn(flags);
        when(evaluator.run(any(Flag.class), eq(context))).thenReturn(null);

        // When
        Map<String, String> result = useCase.run(body, headers, contextBuilder);

        // Then
        assertFalse(result.containsKey(X_FEATURE_FLAG_TAG));
    }

    @Test
    void shouldHandleEmptyFlagsList() {
        // Given
        String body = "test-body";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> context = new HashMap<>();

        when(contextBuilder.run(eq(body), eq(headers))).thenReturn(context);
        when(queryPort.findFlagsByType(Flag.FlagType.STRING)).thenReturn(Collections.emptyList());

        // When
        Map<String, String> result = useCase.run(body, headers, contextBuilder);

        // Then
        assertFalse(result.containsKey(X_FEATURE_FLAG_TAG));
        verify(evaluator, never()).run(any(), any());
    }
}