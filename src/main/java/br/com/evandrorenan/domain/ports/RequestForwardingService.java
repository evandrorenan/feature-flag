package br.com.evandrorenan.domain.ports;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public interface RequestForwardingService {

    ResponseEntity<Object> forward(String encodedUrlParam, HttpServletRequest request, HttpEntity<String> httpEntity);
}
