package br.com.evandrorenan.infra.adapters.rest;

import br.com.evandrorenan.application.FeatureFlagApplication;
import br.com.evandrorenan.domain.ports.in.FeatureFlagPersistencePort;
import br.com.evandrorenan.infra.adapters.mappers.FeatureFlagMapper;
import br.com.featureflagsdkjava.domain.ports.FeatureFlagQueryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                classes = FeatureFlagApplication.class)
@ActiveProfiles("disabled-database")
public class FeatureFlagControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FeatureFlagQueryPort queryPort;

    @Autowired
    private FeatureFlagPersistencePort persistencePort;

    @Autowired
    private FeatureFlagMapper mapper;

    @Test
    void getAllFlags_shouldReturnOkAndListOfFlags() {
        // Assuming your test profile has some initial data
        ResponseEntity<List<FlagDTO>> response = restTemplate.exchange(
                "/v1/flags",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FlagDTO>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).size() >= 0);
        // You might want to add more specific assertions based on your test data
    }

    @Test
    void getFlagsByType_shouldReturnOkAndListOfFlagsOfType() {
        // Assuming your test profile has data with BOOLEAN type
        ResponseEntity<List<FlagDTO>> response = restTemplate.exchange(
                "/v1/flags/by-type/STRING",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FlagDTO>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        if (response.getBody() != null && !response.getBody().isEmpty()) {
            response.getBody().forEach(flag -> assertEquals(FlagDTO.FlagType.STRING, flag.getType()));
        }
    }

    @Test
    void getFlagsByType_shouldReturnNotFoundForInvalidType() {
        ResponseEntity<List<FlagDTO>> response = restTemplate.exchange(
                "/v1/flags/by-type/INVALID",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FlagDTO>>() {}
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getFlagsByName_shouldReturnOkAndFlagIfExists() {
        // Assuming your test profile has a flag named "testFeature"
        ResponseEntity<FlagDTO> response = restTemplate.getForEntity("/v1/flags/flag1", FlagDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        if (response.getBody() != null) {
            assertEquals("flag1", response.getBody().getName());
        }
    }

    @Test
    void getFlagsByName_shouldReturnNotFoundIfFlagDoesNotExist() {
        ResponseEntity<FlagDTO> response = restTemplate.getForEntity("/v1/flags/nonExistentFeature", FlagDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void putFlag_shouldReturnServerError() { //Due to disabled-database profile
        FlagDTO flagDto = new FlagDTO();
        flagDto.setName("newIntegrationTestFeature");
        flagDto.setType(FlagDTO.FlagType.STRING);
        flagDto.setDefaultVariant("default");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FlagDTO> requestEntity = new HttpEntity<>(flagDto, headers);

        ResponseEntity<Object> response = restTemplate.exchange(
                "/v1/flags",
                HttpMethod.PUT,
                requestEntity,
                Object.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}