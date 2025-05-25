package br.com.evandrorenan.infra.adapters.openfeature;

import br.com.evandrorenan.infra.adapters.mappers.FeatureFlagMapper;
import br.com.evandrorenan.infra.adapters.persistence.FeatureFlagRepository;
import br.com.evandrorenan.infra.adapters.persistence.FlagDAO;
import br.com.featureflagsdkjava.domain.model.Flag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseFeatureFlagAdapterTest {

    @Mock
    private FeatureFlagRepository repo;

    @Mock
    private FeatureFlagMapper mapper;

    @InjectMocks
    private DatabaseFeatureFlagAdapter adapter;

    @Test
    void findAll_noFlags_returnsEmptyList() {
        when(repo.findAll()).thenReturn(Collections.emptyList());
        when(mapper.toFlagListFromDAO(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<Flag> result = adapter.findAll();

        assertTrue(result.isEmpty());
        verify(repo, times(1)).findAll();
        verify(mapper, times(1)).toFlagListFromDAO(Collections.emptyList());
    }

    @Test
    void findAll_multipleFlags_returnsMappedList() {
        List<FlagDAO> daos = List.of(
                FlagDAO.builder().id(1L).name("featureA").type(FlagDAO.Type.BOOLEAN).defaultVariant("true").build(),
                FlagDAO.builder().id(2L).name("featureB").type(FlagDAO.Type.STRING).defaultVariant("value").build()
        );
        List<Flag> flags = List.of(
                Flag.builder().id(1L).name("featureA").type(Flag.Type.BOOLEAN).defaultVariant("true").build(),
                Flag.builder().id(2L).name("featureB").type(Flag.Type.STRING).defaultVariant("value").build()
        );
        when(repo.findAll()).thenReturn(daos);
        when(mapper.toFlagListFromDAO(daos)).thenReturn(flags);

        List<Flag> result = adapter.findAll();

        assertEquals(2, result.size());
        assertEquals(flags, result);
        verify(repo, times(1)).findAll();
        verify(mapper, times(1)).toFlagListFromDAO(daos);
    }

    @Test
    void findFlagsByType_existingType_returnsMappedList() {
        FlagDAO dao = FlagDAO.builder()
             .id(1L).name("featureA").type(FlagDAO.Type.BOOLEAN).defaultVariant("true").build();
        List<FlagDAO> daos = Collections.singletonList(dao);
        Flag flag = Flag.builder()
           .id(1L).name("featureA").type(Flag.Type.BOOLEAN).defaultVariant("true").build();
        List<Flag> flags = Collections.singletonList(flag);
        when(repo.findByFlagType(FlagDAO.Type.BOOLEAN)).thenReturn(daos);
        when(mapper.toFlagListFromDAO(daos)).thenReturn(flags);

        List<Flag> result = adapter.findFlagsByType(Flag.Type.BOOLEAN);

        assertEquals(1, result.size());
        assertEquals(flags, result);
        verify(repo, times(1)).findByFlagType(FlagDAO.Type.BOOLEAN);
        verify(mapper, times(1)).toFlagListFromDAO(daos);
    }

    @Test
    void findFlagsByType_nonExistingType_returnsEmptyList() {
        when(repo.findByFlagType(FlagDAO.Type.STRING)).thenReturn(Collections.emptyList());
        when(mapper.toFlagListFromDAO(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<Flag> result = adapter.findFlagsByType(Flag.Type.STRING);

        assertTrue(result.isEmpty());
        verify(repo, times(1)).findByFlagType(FlagDAO.Type.STRING);
        verify(mapper, times(1)).toFlagListFromDAO(Collections.emptyList());
    }

    @Test
    void findByFlagName_existingName_returnsMappedOptionalFlag() {
        FlagDAO dao = FlagDAO.builder()
             .id(1L).name("featureA").type(FlagDAO.Type.BOOLEAN).defaultVariant("true").build();
        Flag flag = Flag.builder()
             .id(1L).name("featureA").type(Flag.Type.BOOLEAN).defaultVariant("true").build();
        when(repo.findByFlagName("featureA")).thenReturn(Optional.of(dao));
        when(mapper.toFlag(dao)).thenReturn(flag);

        Optional<Flag> result = adapter.findByFlagName("featureA");

        assertTrue(result.isPresent());
        assertEquals(flag, result.get());
        verify(repo, times(1)).findByFlagName("featureA");
        verify(mapper, times(1)).toFlag(dao);
    }

    @Test
    void findByFlagName_nonExistingName_returnsEmptyOptional() {
        when(repo.findByFlagName("nonExistent")).thenReturn(Optional.empty());

        Optional<Flag> result = adapter.findByFlagName("nonExistent");

        assertTrue(result.isEmpty());
        verify(repo, times(1)).findByFlagName("nonExistent");
        verifyNoInteractions(mapper);
    }

    @Test
    void findFlagByNameFallback_logsErrorAndReturnsEmptyOptional() {
        String flagName = "failedFeature";
        Throwable exception = new RuntimeException("Database error");

        Optional<FlagDAO> result = adapter.findFlagByNameFallback(flagName, exception);

        assertTrue(result.isEmpty());
    }

    @Test
    void save_validFlag_savesAndReturnsMappedFlag() {
        Flag flagToSave = Flag.builder()
            .name("newFeature").type(Flag.Type.STRING).defaultVariant("initial").build();
        FlagDAO daoToSave = FlagDAO.builder()
            .name("newFeature").type(FlagDAO.Type.STRING).defaultVariant("initial").build();
        FlagDAO savedDAO = FlagDAO.builder()
            .id(3L).name("newFeature").type(FlagDAO.Type.STRING).defaultVariant("initial").build();
        Flag savedFlag = Flag.builder()
            .name("newFeature").type(Flag.Type.STRING).defaultVariant("initial").build();

        when(mapper.toFlagDAO(flagToSave)).thenReturn(daoToSave);
        when(repo.save(daoToSave)).thenReturn(savedDAO);
        when(mapper.toFlag(savedDAO)).thenReturn(savedFlag);

        Flag result = adapter.save(flagToSave);

        assertEquals(savedFlag, result);
        verify(mapper, times(1)).toFlagDAO(flagToSave);
        verify(repo, times(1)).save(daoToSave);
        verify(mapper, times(1)).toFlag(savedDAO);
    }
}