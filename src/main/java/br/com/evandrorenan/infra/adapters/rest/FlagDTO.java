package br.com.evandrorenan.infra.adapters.rest;

import java.util.Map;

public class FlagDTO {

    private Long id;
    private String name;
    private FlagDTO.FlagType type;
    private FlagDTO.State state;
    private String defaultVariant;
    private String targeting;
    private Map<String, Object> variants;

    public enum FlagType {
        BOOLEAN, STRING, NUMBER, OBJECT
    }

    public enum State {
        ENABLED, DISABLED
    }

    public Long getId() {
        return id;
    }

    public FlagDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public FlagDTO setName(String name) {
        this.name = name;
        return this;
    }

    public FlagType getType() {
        return type;
    }

    public FlagDTO setType(FlagType type) {
        this.type = type;
        return this;
    }

    public State getState() {
        return state;
    }

    public FlagDTO setState(State state) {
        this.state = state;
        return this;
    }

    public String getDefaultVariant() {
        return defaultVariant;
    }

    public FlagDTO setDefaultVariant(String defaultVariant) {
        this.defaultVariant = defaultVariant;
        return this;
    }

    public String getTargeting() {
        return targeting;
    }

    public FlagDTO setTargeting(String targeting) {
        this.targeting = targeting;
        return this;
    }

    public Map<String, Object> getVariants() {
        return variants;
    }

    public FlagDTO setVariants(Map<String, Object> variants) {
        this.variants = variants;
        return this;
    }
}