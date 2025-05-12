//package br.com.evandrorenan.infra.adapters.rest;
//
//import br.com.evandrorenan.application.FeatureFlagApplication;
//import br.com.evandrorenan.domain.ports.in.FeatureTagUseCase;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest(classes = FeatureFlagApplication.class)
//class FeatureFlagIntegrationTest {
//
//    @Autowired
//    private FeatureTagController featureTagController;
//
//    @Autowired
//    private FeatureFlagController featureFlagController;
//
//    @Autowired
//    private FeatureTagUseCase featureTagUseCase;
//
//    private static boolean isFlagCreated = false;
//
//    @BeforeAll
//    static void setup(@Autowired FeatureFlagController featureFlagController) {
//        if (!isFlagCreated) {
//            FlagDTO flagDTO = createFlagDTO();
//            featureFlagController.putFlag(flagDTO);
//            isFlagCreated = true;
//        }
//    }
//
//    @Test
//    void tagGetRequest_shouldProcessHeadersAndBodyCorrectly() {
//        // Arrange
//        Map<String, String> requestHeaders = new HashMap<>(Map.of("header1", "value1"));
//        String requestBody = "test-body";
//
//        // Act
//        ResponseEntity<?> response = null;
////                (ResponseEntity<?>) featureTagController.tagGetRequest(requestHeaders, requestBody);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(requestBody, response.getBody());
//
//        HttpHeaders responseHeaders = response.getHeaders();
//        assertHeaderContains(responseHeaders, "header1", "value1");
//    }
//
//    private static FlagDTO createFlagDTO() {
//        FlagDTO flagDTO = new FlagDTO();
//        flagDTO.setName("test-flag");
//        flagDTO.setType(FlagDTO.FlagType.STRING);
//        flagDTO.setState(FlagDTO.State.ENABLED);
//        flagDTO.setDefaultVariant("default-variant");
//        Map<String, Object> variants = Map.of(
//                "default-variant", "DEFAULT",
//                "alternative-variant", "ALTERNATIVE"
//        );
//        flagDTO.setVariants(variants);
//        return flagDTO;
//    }
//
//    private void assertHeaderContains(HttpHeaders headers, String key, String expectedValue) {
//        assertTrue(headers.containsKey(key), "Header does not contain key: " + key);
//        assertEquals(expectedValue, headers.getFirst(key), "Header value mismatch for key: " + key);
//    }
//}
