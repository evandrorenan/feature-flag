package br.com.evandrorenan.domain.ports.in;

import java.util.Map;

public interface ContextBuilder {
    Map<String, String> run(Map<String, String> headers, String body);
}
