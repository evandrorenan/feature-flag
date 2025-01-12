package br.com.evandrorenan.domain.ports.in;

import java.util.Map;

public interface ContextBuilder {
    Map<String, String> run(String body, Map<String, String> headers);
}
