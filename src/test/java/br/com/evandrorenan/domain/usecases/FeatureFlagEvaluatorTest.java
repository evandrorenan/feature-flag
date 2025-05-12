package br.com.evandrorenan.domain.usecases;

import br.com.featureflagsdkjava.domain.model.Flag;
import br.com.featureflagsdkjava.domain.ports.FeatureFlagQueryPort;
import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.ImmutableContext;
import dev.openfeature.sdk.OpenFeatureAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureFlagEvaluatorTest {

    @InjectMocks
    private FeatureFlagEvaluator featureFlagEvaluator;

    @Mock
    private FeatureFlagQueryPort mockedQueryPort;

    @Mock
    private FeatureProvider mockedProvider;

    @Mock
    private Client client;

    @BeforeEach
    void beforeEach() {
        OpenFeatureAPI instance = OpenFeatureAPI.getInstance();
        instance.setProvider(mockedProvider);
    }

    @Test
    void evaluateAllFeatureFlags_emptyFlags_returnsEmptyMap() {
        ImmutableContext context = new ImmutableContext();
        when(mockedQueryPort.findFlagsByType(any())).thenReturn(Collections.emptyList());

        Map<String, String> result = featureFlagEvaluator.evaluateAllFeatureFlagsOfType(context, String.class);

        assertTrue(result.isEmpty());
        verify(mockedQueryPort, times(1)).findFlagsByType(Flag.Type.STRING);
        verifyNoInteractions(client);
    }

    @Test
    void evaluateAllFeatureFlags_singleStringFlag_returnsMapWithStringValue() {
        ImmutableContext context = new ImmutableContext();
        Flag flag = Flag.builder().name("featureA").type(Flag.Type.STRING).build();
        List<Flag> flags = Collections.singletonList(flag);
        when(mockedQueryPort.findFlagsByType(Flag.Type.STRING)).thenReturn(flags);
        when(client.getStringValue("featureA", "", context)).thenReturn("stringValue");

        Map<String, String> result = featureFlagEvaluator.evaluateAllFeatureFlagsOfType(context, String.class);

        assertEquals(1, result.size());
        assertEquals("stringValue", result.get("featureA"));
        verify(mockedQueryPort, times(1)).findFlagsByType(Flag.Type.STRING);
        verify(client, times(1)).getStringValue("featureA", "", context);
    }

    @Test
    void evaluateAllFeatureFlags_singleBooleanFlag_returnsMapWithBooleanValue() {
        ImmutableContext context = new ImmutableContext();
        Flag flag = Flag.builder().name("featureB").type(Flag.Type.BOOLEAN).build();
        List<Flag> flags = Collections.singletonList(flag);
        when(mockedQueryPort.findFlagsByType(Flag.Type.BOOLEAN)).thenReturn(flags);
        when(client.getBooleanValue("featureB", false, context)).thenReturn(true);

        Map<String, Boolean> result = featureFlagEvaluator.evaluateAllFeatureFlagsOfType(context, Boolean.class);

        assertEquals(1, result.size());
        assertTrue(result.get("featureB"));
        verify(mockedQueryPort, times(1)).findFlagsByType(Flag.Type.BOOLEAN);
        verify(client, times(1)).getBooleanValue("featureB", false, context);
    }

    @Test
    void evaluateAllFeatureFlags_singleIntegerFlag_returnsMapWithIntegerValue() {
        ImmutableContext context = new ImmutableContext();
        Flag flag = Flag.builder().name("featureC").type(Flag.Type.NUMBER).build();
        List<Flag> flags = Collections.singletonList(flag);
        when(mockedQueryPort.findFlagsByType(Flag.Type.NUMBER)).thenReturn(flags);
        when(client.getIntegerValue("featureC", 0, context)).thenReturn(123);

        Map<String, Integer> result = featureFlagEvaluator.evaluateAllFeatureFlagsOfType(context, Integer.class);

        assertEquals(1, result.size());
        assertEquals(123, result.get("featureC"));
        verify(mockedQueryPort, times(1)).findFlagsByType(Flag.Type.NUMBER);
        verify(client, times(1)).getIntegerValue("featureC", 0, context);
    }

    @Test
    void evaluateAllFeatureFlags_singleDoubleFlag_returnsMapWithDoubleValue() {
        ImmutableContext context = new ImmutableContext();
        Flag flag = Flag.builder().name("featureD").type(Flag.Type.NUMBER).build();
        List<Flag> flags = Collections.singletonList(flag);
        when(mockedQueryPort.findFlagsByType(Flag.Type.NUMBER)).thenReturn(flags);
        when(client.getDoubleValue("featureD", 0.0, context)).thenReturn(3.14);

        Map<String, Double> result = featureFlagEvaluator.evaluateAllFeatureFlagsOfType(context, Double.class);

        assertEquals(1, result.size());
        assertEquals(3.14, result.get("featureD"), 0.001);
        verify(mockedQueryPort, times(1)).findFlagsByType(Flag.Type.NUMBER);
        verify(client, times(1)).getDoubleValue("featureD", 0.0, context);
    }

    @Test
    void evaluateAllFeatureFlags_multipleFlags_returnsMapWithAllEvaluatedValues() {
        ImmutableContext context = new ImmutableContext();
        Flag flagA = Flag.builder().name("featureA").type(Flag.Type.STRING).build();
        Flag flagB = Flag.builder().name("featureB").type(Flag.Type.STRING).build();
        List<Flag> allFlags = Arrays.asList(flagA, flagB);
        when(mockedQueryPort.findFlagsByType(any())).thenReturn(allFlags);
        when(client.getStringValue("featureA", "", context)).thenReturn("valueA");
        when(client.getStringValue("featureB", "", context)).thenReturn("valueB");

        Map<String, String> result = featureFlagEvaluator.evaluateAllFeatureFlagsOfType(context, String.class);

        assertEquals(2, result.size());
        assertEquals("valueA", result.get("featureA"));
        assertEquals("valueB", result.get("featureB"));
        verify(mockedQueryPort, times(1)).findFlagsByType(Flag.Type.STRING);
        verify(client, times(1)).getStringValue("featureA", "", context);
        verify(client, times(1)).getStringValue("featureB", "", context);
    }

    @Test
    void evaluateFeatureFlag_unsupportedType_throwsIllegalArgumentException() {
        ImmutableContext context = new ImmutableContext();
        Flag flagA = Flag.builder().name("featureA").type(Flag.Type.OBJECT).build();
        when(mockedQueryPort.findFlagsByType(any())).thenReturn(List.of(flagA));

        assertThrows(IllegalArgumentException.class, () -> featureFlagEvaluator.evaluateAllFeatureFlagsOfType(context, Object.class));
    }

    @Test
    void getType_stringType_returnsStringType() {
        assertEquals(Flag.Type.STRING, FeatureFlagEvaluator.getType(String.class));
    }

    @Test
    void getType_booleanType_returnsBooleanType() {
        assertEquals(Flag.Type.BOOLEAN, FeatureFlagEvaluator.getType(Boolean.class));
    }

    @Test
    void getType_integerType_returnsNumberType() {
        assertEquals(Flag.Type.NUMBER, FeatureFlagEvaluator.getType(Integer.class));
    }

    @Test
    void getType_doubleType_returnsNumberType() {
        assertEquals(Flag.Type.NUMBER, FeatureFlagEvaluator.getType(Double.class));
    }

    @Test
    void getType_unsupportedType_returnsObjectType() {
        assertEquals(Flag.Type.OBJECT, FeatureFlagEvaluator.getType(Object.class));
    }
}