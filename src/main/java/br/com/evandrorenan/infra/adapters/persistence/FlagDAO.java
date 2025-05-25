package br.com.evandrorenan.infra.adapters.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@Entity
@Table(name = "flags")
public class FlagDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flag_name", nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "flag_type", nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state;

    @Column(name = "default_variant", nullable = false)
    private String defaultVariant;

    @Column(name = "targeting", columnDefinition = "jsonb")
    private String targeting;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "variants", joinColumns = @JoinColumn(name = "flag_id"))
    @MapKeyColumn(name = "variant_key")
    @Column(name = "variant_value")
    private Map<String, Object> variants;

    public enum Type {
        BOOLEAN, STRING, NUMBER, OBJECT
    }

    public enum State {
        ENABLED, DISABLED
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public State getState() {
        return state;
    }

    public String getDefaultVariant() {
        return defaultVariant;
    }

    public String getTargeting() {
        return targeting;
    }

    public Map<String, Object> getVariants() {
        return variants;
    }

}