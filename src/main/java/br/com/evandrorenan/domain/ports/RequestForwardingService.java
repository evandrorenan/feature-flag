package br.com.evandrorenan.domain.ports;

import br.com.evandrorenan.domain.model.ProxyRequestContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public interface RequestForwardingService {

    ResponseEntity<Object> forward(ProxyRequestContext proxyRequestContext);
}
