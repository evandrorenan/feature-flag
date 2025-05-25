//package br.com.evandrorenan.infra.adapters.openfeature;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static br.com.evandrorenan.infra.adapters.openfeature.RequestDataContextBuilder.REQUEST_KEY;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class RequestDataContextBuilderTest {
//
//    private final RequestDataContextBuilder contextBuilder =
//        new RequestDataContextBuilder(new ObjectMapper());
//
//    @Test
//    @DisplayName("shouldReturnSameHeadersWhenBodyIsNull")
//    void shouldReturnHeadersWhenBodyIsNull() {
//        Map<String, String> headers = new HashMap<>();
//        headers.put("HeaderKey", "HeaderValue");
//
//        Map<String, String> result = contextBuilder.run(headers, null);
//
//        assertEquals(headers, result);
//    }
//
//    @Test
//    @DisplayName("shouldAddRequestBodyAsTextWhenBodyIsNotJson")
//    void shouldAddRequestBodyAsTextWhenBodyIsNotJson() {
//        String body = "Not a JSON body";
//        Map<String, String> headers = new HashMap<>();
//        headers.put("HeaderKey", "HeaderValue");
//
//        Map<String, String> result = contextBuilder.run(headers, body);
//
//        assertEquals("Not a JSON body", result.get(REQUEST_KEY));
//        assertEquals("HeaderValue", result.get("HeaderKey"));
//    }
//
//    @Test
//    @DisplayName("shouldReturnParsedJsonWhenBodyIsValidJson")
//    void shouldReturnParsedJsonWhenBodyIsValidJson() {
//        String body = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
//        Map<String, String> headers = new HashMap<>();
//        headers.put("HeaderKey", "HeaderValue");
//
//        Map<String, String> result = contextBuilder.run(headers, body);
//
//        assertEquals("value1", result.get("key1"));
//        assertEquals("value2", result.get("key2"));
//        assertEquals("HeaderValue", result.get("HeaderKey"));
//    }
//
//    @Test
//    @DisplayName("shouldHandleJsonProcessingExceptionGracefully")
//    void shouldHandleJsonProcessingExceptionGracefully() throws JsonProcessingException {
//        // Mocking ObjectMapper to throw exception
//        ObjectMapper mockedMapper = Mockito.mock(ObjectMapper.class);
//
//        String body = "{\"key1\":\"value1\"}";
//        TypeReference<Map<String, Object>> typeReference = new TypeReference<>() {};
//        Mockito.when(mockedMapper.readValue(body, typeReference))
//               .thenThrow(new JsonProcessingException("Mock Exception") {});
//
//        OpenFeatureContextBuilder contextBuilderWithMock =
//                new OpenFeatureContextBuilder(mockedMapper);
//
//        Map<String, String> headers = new HashMap<>();
//        Map<String, String> result = contextBuilderWithMock.evaluateAllFeatureFlagsOfType(body, headers);
//
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("shouldIdentifyJsonRequestCorrectly")
//    void shouldIdentifyJsonRequestCorrectly() {
//        String validJsonObject = "{\"key\":\"value\", \"key2\": \"value2\", \"key3\": [ \"value3_0\", \"value3_1\" ] }";
//        String validJsonArray = "[\"value1\",\"value2\"]";
//        String textRequest = "Not a JSON";
//        Map<String, String> headers = new HashMap<>();
//
//        Map<String, String> resultForObject = contextBuilder.evaluateAllFeatureFlagsOfType(validJsonObject, headers);
//        Map<String, String> resultForArray = contextBuilder.evaluateAllFeatureFlagsOfType(validJsonArray, headers);
//        Map<String, String> resultForInvalid = contextBuilder.evaluateAllFeatureFlagsOfType(textRequest, headers);
//
//        assertEquals(3, resultForObject.size());
//        assertTrue(resultForArray.isEmpty());
//        assertEquals(textRequest, resultForInvalid.get(OpenFeatureContextBuilder.REQUEST));
//    }
//}
