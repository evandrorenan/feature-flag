package br.com.evandrorenan.infra.adapters.rest;

import br.com.evandrorenan.domain.ports.in.ContextBuilder;
import br.com.evandrorenan.domain.ports.in.FeatureTagUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Feature Flag", description = "Feature Flag Tag Management API")
public class FeatureTagController {

    private final FeatureTagUseCase featureTagUseCase;

    @Autowired
    public FeatureTagController(FeatureTagUseCase featureTagUseCase, ContextBuilder contextBuilder) {
        this.featureTagUseCase = featureTagUseCase;
    }

    @RequestMapping(value = "/v1/tag-request")
    public Object tagGetRequest(@RequestHeader Map<String, String> headers, @RequestBody(required = false) String body) {
        Map<String, String> newHeaders = featureTagUseCase.run(headers, body);
        headers.putAll(newHeaders);
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::add);

        return new ResponseEntity<>(body, httpHeaders, HttpStatus.OK);
    }
}
