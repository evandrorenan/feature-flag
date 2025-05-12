package br.com.evandrorenan.infra.adapters.rest;

import br.com.evandrorenan.domain.ports.in.FeatureFlagPersistencePort;
import br.com.evandrorenan.infra.adapters.mappers.FeatureFlagMapper;
import br.com.featureflagsdkjava.domain.model.Flag;
import br.com.featureflagsdkjava.domain.ports.FeatureFlagQueryPort;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Tag(name = "Feature Flag", description = "Feature Flag Management API")
public class FeatureFlagController {

    private final FeatureFlagQueryPort queryPort;
    private final FeatureFlagPersistencePort persistencePort;
    private final FeatureFlagMapper mapper;

    @Autowired
    public FeatureFlagController(
            @Qualifier("databaseFeatureFlagQuery") FeatureFlagQueryPort queryPort,
            FeatureFlagPersistencePort persistencePort, FeatureFlagMapper mapper) {
        this.queryPort = queryPort;
        this.persistencePort = persistencePort;
        this.mapper = mapper;
    }

    @GetMapping("/v1/flags")
    public ResponseEntity<List<Flag>> getAllFlags() {
        log.info("getAllFlags");
        return ResponseEntity.ok(queryPort.findAll());
    }

    @GetMapping("/v1/flags/by-type/{flagType}")
    public ResponseEntity<List<Flag>> getFlagsByType(@PathVariable(name = "type") String flagType) {
        try {
            Flag.Type type = Flag.Type.valueOf(flagType);
            return ResponseEntity.ok(queryPort.findFlagsByType(type));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/v1/flags/{flagName}")
    public ResponseEntity<Flag> getFlagByName(@PathVariable(name = "flagName") String flagName) {
        Optional<Flag> optFlag = queryPort.findByFlagName(flagName);
        if (optFlag.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(optFlag.get());
    }

    @PutMapping("/v1/flags")
    public ResponseEntity<Flag> putFlag(@RequestBody FlagDTO flagDto) {
        Flag flag = mapper.toFlag(flagDto);
        Flag savedFlag = persistencePort.save(flag);
        return ResponseEntity.ok(savedFlag);
    }
}