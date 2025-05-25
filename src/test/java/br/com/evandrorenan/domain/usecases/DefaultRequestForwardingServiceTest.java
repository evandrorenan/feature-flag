package br.com.evandrorenan.domain.usecases;

import br.com.evandrorenan.domain.model.ProxyRequestContext;
import br.com.evandrorenan.domain.ports.RequestForwardingService;
import br.com.evandrorenan.domain.ports.out.RequestDetailsLogger;
import br.com.evandrorenan.infra.adapters.log.DefaultRequestDetailsLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.exceptions.InvalidContextError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultRequestForwardingServiceTest {

    @Mock
    private Client mockedClient;

    @Mock
    private RestTemplate mockedRestTemplate;

    private final ObjectMapper mapper = new ObjectMapper();
    private final RequestDetailsLogger logger = new DefaultRequestDetailsLogger();
    private RequestForwardingService requestForwardingService;
    private MockHttpServletRequest mockedRequest;
    private HttpEntity<String> httpEntity;
    private final String featureFlagName = "testFeature";
    private final String destinationUrl = "http://example.com/api";

    @BeforeEach
    void setUp() {
        mockedRequest = new MockHttpServletRequest("GET", "/v1/proxy/" + featureFlagName + "/some/path?param1=val1&param2=val2");
        mockedRequest.addHeader("Content-Type", "application/json");
        mockedRequest.addHeader("Authorization", "Bearer token");

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer token");
        String requestBody = "{\"key\": \"value\"}";

        httpEntity = new HttpEntity<>(requestBody, headers);

        requestForwardingService = new DefaultRequestForwardingService(
                mockedRestTemplate, mapper, mockedClient, logger);
    }

    @Test
    void forward_successfulRequest() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<byte[]> mockResponseEntity = new ResponseEntity<>("{\"response\": \"success\"}".getBytes(), responseHeaders, HttpStatus.OK);

        when(mockedClient.getStringValue(eq(featureFlagName), eq(""), any())).thenReturn(destinationUrl);
        when(mockedRestTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(byte[].class)
        )).thenReturn(mockResponseEntity);

        ResponseEntity<Object> actualResponse = requestForwardingService.forward(new ProxyRequestContext(featureFlagName, mockedRequest, httpEntity));

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, actualResponse.getHeaders().getContentType());
        assertEquals("{\"response\": \"success\"}", new String((byte[]) actualResponse.getBody()));

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(mockedRestTemplate, times(1)).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(byte[].class)
        );
        assertEquals(URI.create(destinationUrl + "/some/path?param1=val1&param2=val2"), uriCaptor.getValue());
    }

    @Test
    void forward_postRequestWithBodyAndHeaders() {
        mockedRequest = new MockHttpServletRequest("POST", "/v1/proxy/" + featureFlagName + "/other/path");
        mockedRequest.setContentType("application/xml");
        mockedRequest.addHeader("X-Custom-Header", "customValue");
        String body = "<xml>data</xml>";
        mockedRequest.setContent(body.getBytes());

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/xml");
        headers.add("X-Custom-Header", "customValue");

        httpEntity = new HttpEntity<>("<xml>data</xml>", headers);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_XML);
        ResponseEntity<byte[]> mockResponseEntity = new ResponseEntity<>("<response>ok</response>".getBytes(), responseHeaders, HttpStatus.CREATED);

        when(mockedClient.getStringValue(eq(featureFlagName), eq(""), any())).thenReturn(destinationUrl);
        when(mockedRestTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(byte[].class)
        )).thenReturn(mockResponseEntity);

        ProxyRequestContext proxyRequestContext = new ProxyRequestContext(featureFlagName, mockedRequest, httpEntity);
        ResponseEntity<Object> actualResponse = requestForwardingService.forward(proxyRequestContext);

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
        assertEquals(MediaType.APPLICATION_XML, actualResponse.getHeaders().getContentType());
        assertEquals("<response>ok</response>", new String((byte[]) actualResponse.getBody()));
    }

    @Test
    void forward_noDestinationUrlFound() {
        when(mockedClient.getStringValue(eq(featureFlagName), eq(""), any())).thenReturn(null);

        assertThrows(InvalidContextError.class, () ->
                requestForwardingService.forward(new ProxyRequestContext(featureFlagName, mockedRequest, httpEntity)));
    }

    @Test
    void forward_emptyDestinationUrlFound() {
        when(mockedClient.getStringValue(eq(featureFlagName), eq(""), any())).thenReturn("");

        assertThrows(InvalidContextError.class, () ->
                requestForwardingService.forward(new ProxyRequestContext(featureFlagName, mockedRequest, httpEntity)));
    }

    @Test
    void forward_requestWithNoExtraPathAndParams() {
        mockedRequest = new MockHttpServletRequest("PUT", "/v1/proxy/" + featureFlagName);
        httpEntity = new HttpEntity<>("updated data", new HttpHeaders());
        ResponseEntity<byte[]> mockResponseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        when(mockedClient.getStringValue(eq(featureFlagName), eq(""), any())).thenReturn(destinationUrl);
        when(mockedRestTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(byte[].class)
        )).thenReturn(mockResponseEntity);

        requestForwardingService.forward(new ProxyRequestContext(featureFlagName, mockedRequest, httpEntity));

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(mockedRestTemplate, times(1)).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(byte[].class)
        );
        assertEquals(URI.create(destinationUrl), uriCaptor.getValue());
    }

    @Test
    void forward_requestWithOnlyParams() {
        mockedRequest = new MockHttpServletRequest("DELETE", "/v1/proxy/" + featureFlagName + "?filter=true&sort=name");
        httpEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<byte[]> mockResponseEntity = new ResponseEntity<>(HttpStatus.ACCEPTED);

        when(mockedClient.getStringValue(eq(featureFlagName), eq(""), any())).thenReturn(destinationUrl);
        when(mockedRestTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(byte[].class)
        )).thenReturn(mockResponseEntity);

        requestForwardingService.forward(new ProxyRequestContext(featureFlagName, mockedRequest, httpEntity));

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(mockedRestTemplate, times(1)).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(byte[].class)
        );
        assertEquals(URI.create(destinationUrl + "?filter=true&sort=name"), uriCaptor.getValue());
    }

    @Test
    void forward_requestWithDifferentHttpMethod() {
        mockedRequest = new MockHttpServletRequest("PATCH", "/v1/proxy/" + featureFlagName + "/resource");
        httpEntity = new HttpEntity<>("partial update", new HttpHeaders());
        ResponseEntity<byte[]> mockResponseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(mockedClient.getStringValue(eq(featureFlagName), eq(""), any())).thenReturn(destinationUrl);
        when(mockedRestTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(byte[].class)
        )).thenReturn(mockResponseEntity);

        requestForwardingService.forward(new ProxyRequestContext(featureFlagName, mockedRequest, httpEntity));

        verify(mockedRestTemplate, times(1)).exchange(
                any(URI.class),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(byte[].class)
        );
    }

//    @Test
//    void buildURI_withExtraPathAndParams() {
//        ProxyRequestContext ctx = new ProxyRequestContext(
//                featureFlagName,
//                new MockHttpServletRequest("GET", "/v1/proxy/" + featureFlagName + "/api/items?id=123&type=product"),
//                new HttpEntity<>(null, new HttpHeaders())
//        );
//        URI expectedUri = URI.create(destinationUrl + "/api/items?id=123&type=product");
//        URI actualUri = DefaultRequestForwardingService.buildURI(destinationUrl, ctx, "/api/items");
//        assertEquals(expectedUri, actualUri);
//    }
//
//    @Test
//    void buildURI_withOnlyExtraPath() {
//        ProxyRequestContext ctx = new ProxyRequestContext(
//                featureFlagName,
//                new MockHttpServletRequest("GET", "/v1/proxy/" + featureFlagName + "/users"),
//                new HttpEntity<>(null, new HttpHeaders())
//        );
//        URI expectedUri = URI.create(destinationUrl + "/users");
//        URI actualUri = DefaultRequestForwardingService.buildURI(destinationUrl, ctx, "/users");
//        assertEquals(expectedUri, actualUri);
//    }
//
//    @Test
//    void buildURI_withOnlyParams() {
//        ProxyRequestContext ctx = new ProxyRequestContext(
//                featureFlagName,
//                new MockHttpServletRequest("GET", "/v1/proxy/" + featureFlagName + "?sort=price&order=desc"),
//                new HttpEntity<>(null, new HttpHeaders())
//        );
//        URI expectedUri = URI.create(destinationUrl + "?sort=price&order=desc");
//        URI actualUri = DefaultRequestForwardingService.buildURI(destinationUrl, ctx, "");
//        assertEquals(expectedUri, actualUri);
//    }
//
//    @Test
//    void buildURI_withNoExtraPathAndNoParams() {
//        ProxyRequestContext ctx = new ProxyRequestContext(
//                featureFlagName,
//                new MockHttpServletRequest("GET", "/v1/proxy/" + featureFlagName),
//                new HttpEntity<>(null, new HttpHeaders())
//        );
//        URI expectedUri = URI.create(destinationUrl);
//        URI actualUri = DefaultRequestForwardingService.buildURI(destinationUrl, ctx, "");
//        assertEquals(expectedUri, actualUri);
//    }

//    @Test
//    void toMultiValueMap_withParams() {
//        Map<String, String[]> rawParams = new HashMap<>();
//        rawParams.put("param1", new String[]{"value1", "valueA"});
//        rawParams.put("param2", new String[]{"value2"});
//        MultiValueMap<String, String> expected = new LinkedMultiValueMap<>();
//        expected.add("param1", "value1");
//        expected.add("param1", "valueA");
//        expected.add("param2", "value2");
//        assertEquals(expected, DefaultRequestForwardingService.toMultiValueMap(rawParams));
//    }
//
//    @Test
//    void toMultiValueMap_withNullValues() {
//        Map<String, String[]> rawParams = new HashMap<>();
//        rawParams.put("param1", null);
//        MultiValueMap<String, String> expected = new LinkedMultiValueMap<>();
//        assertEquals(expected, DefaultRequestForwardingService.toMultiValueMap(rawParams));
//    }
//
//    @Test
//    void toMultiValueMap_withEmptyParams() {
//        Map<String, String[]> rawParams = Collections.emptyMap();
//        MultiValueMap<String, String> expected = new LinkedMultiValueMap<>();
//        assertEquals(expected, DefaultRequestForwardingService.toMultiValueMap(rawParams));
//    }
}