package br.com.evandrorenan.infra.adapters.log;

import br.com.evandrorenan.domain.model.ProxyRequestContext;
import br.com.evandrorenan.domain.ports.out.RequestDetailsLogger;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DefaultRequestDetailsLogger implements RequestDetailsLogger {

    @Override
    public void log(ProxyRequestContext proxyRequestContext) {
        log.info("Proxy Request -> Method: {}, URI: {}", proxyRequestContext.getHttpMethod(), proxyRequestContext.getRequestPath());
        log.info("Headers: {}", obfuscateAuthorization(proxyRequestContext));
        log.debug("Body: {}", proxyRequestContext.getRequestBody());
    }

    private static Map<String, String> obfuscateAuthorization(ProxyRequestContext proxyRequestContext) {
        Map<String, String> unmodifiableMap = proxyRequestContext.getRequestHeaders().asSingleValueMap();
        Map<String, String> headersMap = new HashMap<>(unmodifiableMap);
        String authorization = headersMap.get("Authorization");

        if (authorization == null || authorization.length() < 10) {
            return headersMap;
        }

        String firstTen = authorization.substring(0, 10);

        headersMap.put("Authorization", firstTen + "...(length:" + authorization.length() + ")");
        return headersMap;
    }
}