package br.com.evandrorenan.infra.adapters.openfeature;

import br.com.evandrorenan.domain.ports.in.FeatureFlagPersistencePort;
import br.com.evandrorenan.infra.adapters.mappers.FeatureFlagMapper;
import br.com.evandrorenan.infra.adapters.persistence.FeatureFlagRepository;
import br.com.evandrorenan.infra.adapters.persistence.FlagDAO;
import br.com.featureflagsdkjava.domain.model.Flag;
import br.com.featureflagsdkjava.domain.ports.FeatureFlagQueryPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("database")
@Primary
public class DatabaseFeatureFlagAdapter implements FeatureFlagQueryPort, FeatureFlagPersistencePort {

    private final FeatureFlagRepository repo;
    private final FeatureFlagMapper mapper;

    public DatabaseFeatureFlagAdapter(FeatureFlagRepository repo, FeatureFlagMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<Flag> findAll() {
        List<FlagDAO> flagList = new ArrayList<>();

        Iterable<FlagDAO> flags = repo.findAll();
        flags.forEach(flagList::add);
        return mapper.toFlagListFromDAO(flagList);
    }

    @Override
    public List<Flag> findFlagsByType(Flag.FlagType flagType) {
        return mapper.toFlagListFromDAO(repo.findByFlagType(FlagDAO.FlagType.valueOf(flagType.name())));
    }

    @CircuitBreaker(name = "databaseFeatureFlagAdapter", fallbackMethod = "findFlagByNameFallback")
    public Optional<Flag> findByFlagName(String flagName) {
        Optional<FlagDAO> optFlagDAO = repo.findByFlagName(flagName);

        if (optFlagDAO.isEmpty()) return Optional.empty();
        return Optional.of(mapper.toFlag(optFlagDAO.get()));
    }

    public Optional<FlagDAO> findFlagByNameFallback(String flagName, Throwable e) {
        log.error("Fallback triggered when trying to fetch flag {} due to: {}", flagName, e);
        return Optional.empty();
    }

    @Override
    public Flag save(Flag flag) {
        FlagDAO flagDAO = mapper.toFlagDAO(flag);
        FlagDAO savedFlag = repo.save(flagDAO);
        return mapper.toFlag(savedFlag);
    }
}
