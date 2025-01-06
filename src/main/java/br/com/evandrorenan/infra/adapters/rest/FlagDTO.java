package br.com.evandrorenan.infra.adapters.rest;

import java.util.Map;

public class FlagDTO {

    private Long id;
    private String flagName;
    private FlagDTO.FlagType flagType;
    private FlagDTO.State state;
    private String defaultVariant;
    private String targeting;
    private Map<String, String> variants;

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

    public String getFlagName() {
        return flagName;
    }

    public FlagDTO setFlagName(String flagName) {
        this.flagName = flagName;
        return this;
    }

    public FlagType getFlagType() {
        return flagType;
    }

    public FlagDTO setFlagType(FlagType flagType) {
        this.flagType = flagType;
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

    public Map<String, String> getVariants() {
        return variants;
    }

    public FlagDTO setVariants(Map<String, String> variants) {
        this.variants = variants;
        return this;
    }
}