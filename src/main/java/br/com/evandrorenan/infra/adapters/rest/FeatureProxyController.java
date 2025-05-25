package br.com.evandrorenan.infra.adapters.rest;

import br.com.evandrorenan.domain.model.ProxyRequestContext;
import br.com.evandrorenan.domain.ports.RequestForwardingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FeatureProxyController {

    private final RequestForwardingService requestProxy;

    @Autowired
    public FeatureProxyController(RequestForwardingService requestProxy) {
        this.requestProxy = requestProxy;
    }

    @RequestMapping(
            path = "/v1/proxy/{featureFlagName}/**",
            method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }
    )
    public ResponseEntity<Object> handleProxyRequest(
            HttpServletRequest request,
            HttpEntity<String> httpEntity,
            @PathVariable("featureFlagName") String featureFlagName) {

        return requestProxy.forward(new ProxyRequestContext(featureFlagName, request, httpEntity));
    }
}
