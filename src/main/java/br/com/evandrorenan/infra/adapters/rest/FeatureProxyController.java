package br.com.evandrorenan.infra.adapters.rest;

import br.com.evandrorenan.domain.ports.RequestForwardingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@RestController
public class FeatureProxyController {

    private final RequestForwardingService requestProxy;

    @Autowired
    public FeatureProxyController(RequestForwardingService requestProxy) {
        this.requestProxy = requestProxy;
    }

    @PostMapping("/v1/base64/encode")
    public String base64Encode(@RequestBody String body) {
        return Base64.getUrlEncoder().encodeToString(body.getBytes(StandardCharsets.UTF_8));
    }

    @RequestMapping(
            path = "/v1/proxy/{encodedUrlParam}/**",
            method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }
    )
    public ResponseEntity<Object> handleProxyRequest(
            HttpServletRequest request,
            HttpEntity<String> httpEntity,
            @PathVariable("encodedUrlParam") String encodedUrlParam) {

        return requestProxy.forward(encodedUrlParam, request, httpEntity);
    }
}
