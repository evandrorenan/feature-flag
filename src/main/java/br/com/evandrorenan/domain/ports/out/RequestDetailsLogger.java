package br.com.evandrorenan.domain.ports.out;

import br.com.evandrorenan.domain.model.ProxyRequestContext;

public interface RequestDetailsLogger {

    void log(ProxyRequestContext proxyRequestContext);
}