package br.com.evandrorenan.domain.usecases;

import br.com.evandrorenan.domain.ports.in.EvaluateFeatureFlagsUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static br.com.evandrorenan.domain.ports.in.FeatureTagUseCase.X_FEATURE_FLAG_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultFeatureTagUseCaseTest {

    @Mock
    private EvaluateFeatureFlagsUseCase evaluateStringFlagsUseCase;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private DefaultFeatureTagUseCase featureTagUseCase;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpEntity<String> httpEntity;
    private MockHttpServletRequest mockedRequest;

    @BeforeEach
    void setUp() {
        mockedRequest = new MockHttpServletRequest("GET", "/v1/proxy/some/path?param1=val1&param2=val2");
        mockedRequest.addHeader("Content-Type", "application/json");
        mockedRequest.addHeader("Authorization", "Bearer token");

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type1", "application/json");
        headers.add("Authorization", "Bearer token");
        String requestBody = "{\"key\": \"value\"}";

        httpEntity = new HttpEntity<>(requestBody, headers);
    }

    @Test
    void run_headerAlreadyPresent_returnsExistingHeaders() {
        EvaluateFeatureFlagsUseCase mockedEvaluateFFUseCase = mock(EvaluateFeatureFlagsUseCase.class);

        DefaultFeatureTagUseCase featureTagUseCase = new DefaultFeatureTagUseCase(mockedEvaluateFFUseCase, new ObjectMapper());

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type1", "application/json");
        headers.add("Authorization", "Bearer token");
        headers.add(X_FEATURE_FLAG_TAG, "tag");
        String requestBody = "{\"key\": \"value\"}";
        httpEntity = new HttpEntity<>(requestBody, headers);

        Map<String, String> newHeaders = featureTagUseCase.run(mockedRequest, httpEntity);

        assertTrue(matchOriginalHeaders(newHeaders));
        assertTrue(matchNewHeaders(newHeaders));
    }

    @Test
    void run_noMatchingFlags_returnsOriginalHeaders() {
        EvaluateFeatureFlagsUseCase mockedEvaluateFFUseCase = mock(EvaluateFeatureFlagsUseCase.class);
        when(mockedEvaluateFFUseCase.evaluateAllFeatureFlagsOfType(any(), any()))
                .thenReturn(Collections.emptyMap());

        DefaultFeatureTagUseCase featureTagUseCase = new DefaultFeatureTagUseCase(mockedEvaluateFFUseCase, new ObjectMapper());

        Map<String, String> newHeaders = featureTagUseCase.run(mockedRequest, httpEntity);

        assertTrue(matchOriginalHeaders(newHeaders));
        assertTrue(matchNewHeaders(newHeaders));
    }

    @Test
    void run_singleMatchingFlag_addsTagHeader() {
        EvaluateFeatureFlagsUseCase mockedEvaluateFFUseCase = mock(EvaluateFeatureFlagsUseCase.class);
        when(mockedEvaluateFFUseCase.evaluateAllFeatureFlagsOfType(any(), any()))
                .thenReturn(Map.of("flag1", "tag1"));

        DefaultFeatureTagUseCase featureTagUseCase = new DefaultFeatureTagUseCase(mockedEvaluateFFUseCase, new ObjectMapper());

        Map<String, String> newHeaders = featureTagUseCase.run(mockedRequest, httpEntity);

        assertTrue(newHeaders.containsKey(X_FEATURE_FLAG_TAG));
        assertEquals("tag1", newHeaders.get(X_FEATURE_FLAG_TAG));
        newHeaders.remove(X_FEATURE_FLAG_TAG);
        assertTrue(matchOriginalHeaders(newHeaders));
        assertTrue(matchNewHeaders(newHeaders));
    }

    @Test
    void run_multipleMatchingFlags_returnsOriginalHeaders() {
        EvaluateFeatureFlagsUseCase mockedEvaluateFFUseCase = mock(EvaluateFeatureFlagsUseCase.class);
        when(mockedEvaluateFFUseCase.evaluateAllFeatureFlagsOfType(any(), any()))
                .thenReturn(Map.of("flag1", "tag1", "flag2", "tag2"));

        DefaultFeatureTagUseCase featureTagUseCase = new DefaultFeatureTagUseCase(mockedEvaluateFFUseCase, new ObjectMapper());

        Map<String, String> newHeaders = featureTagUseCase.run(mockedRequest, httpEntity);

        assertTrue(matchOriginalHeaders(newHeaders));
        assertTrue(matchNewHeaders(newHeaders));
    }

    @Test
    void run_matchingFlagWithNullOrEmptyValue_returnsOriginalHeaders() {
        EvaluateFeatureFlagsUseCase mockedEvaluateFFUseCase = mock(EvaluateFeatureFlagsUseCase.class);
        when(mockedEvaluateFFUseCase.evaluateAllFeatureFlagsOfType(any(), any()))
                .thenReturn(Map.of("flag1", ""));

        DefaultFeatureTagUseCase featureTagUseCase = new DefaultFeatureTagUseCase(mockedEvaluateFFUseCase, new ObjectMapper());

        Map<String, String> newHeaders = featureTagUseCase.run(mockedRequest, httpEntity);

        assertTrue(matchOriginalHeaders(newHeaders));
        assertTrue(matchNewHeaders(newHeaders));
    }

//    @Test
//    void shouldAddHeader_emptyTags_returnsFalse() {
//        Map<String, String> emptyTags = Collections.emptyMap();
//        assertFalse(DefaultFeatureTagUseCase.shouldAddHeader(emptyTags));
//    }
//
//    @Test
//    void shouldAddHeader_multipleTags_returnsFalse() {
//        Map<String, String> multipleTags = Map.of("featureA", "tagA", "featureB", "tagB");
//        assertFalse(DefaultFeatureTagUseCase.shouldAddHeader(multipleTags));
//    }
//
//    @Test
//    void shouldAddHeader_singleTagNullValue_returnsFalse() {
//        Map<String, String> singleTagNull = Collections.singletonMap("featureA", null);
//        assertFalse(DefaultFeatureTagUseCase.shouldAddHeader(singleTagNull));
//    }
//
//    @Test
//    void shouldAddHeader_singleTagEmptyValue_returnsFalse() {
//        Map<String, String> singleTagEmpty = Collections.singletonMap("featureA", "");
//        assertFalse(DefaultFeatureTagUseCase.shouldAddHeader(singleTagEmpty));
//    }
//
//    @Test
//    void shouldAddHeader_singleTagWithValue_returnsTrue() {
//        Map<String, String> singleTagWithValue = Collections.singletonMap("featureA", "validTag");
//        assertTrue(DefaultFeatureTagUseCase.shouldAddHeader(singleTagWithValue));
//    }

    private boolean matchNewHeaders(Map<String, String> newHeaders) {
        return httpEntity.getHeaders().entrySet().stream()
                         .allMatch(entry -> newHeaders.containsKey(entry.getKey())
                                 && entry.getValue().size() == 1
                                 && Objects.equals(entry.getValue().get(0), newHeaders.get(entry.getKey())));
    }

    private boolean matchOriginalHeaders(Map<String, String> newHeaders) {
        return newHeaders.entrySet().stream()
                         .allMatch(entry -> httpEntity.getHeaders().containsKey(entry.getKey())
                                 && httpEntity.getHeaders().get(entry.getKey()).contains(entry.getValue()));
    }
}