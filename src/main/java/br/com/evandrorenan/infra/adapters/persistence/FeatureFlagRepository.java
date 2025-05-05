package br.com.evandrorenan.infra.adapters.persistence;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@Profile("!disabled-database")
public interface FeatureFlagRepository extends CrudRepository<FlagDAO, Long> {
    @Query("""
        SELECT DISTINCT f
        FROM FlagDAO f
        JOIN f.variants fv
        WHERE f.flagType = 'STRING'
    """)
    List<FlagDAO> findByFlagType(FlagDAO.FlagType flagType);

    Optional<FlagDAO> findByFlagName(String flagName);
}