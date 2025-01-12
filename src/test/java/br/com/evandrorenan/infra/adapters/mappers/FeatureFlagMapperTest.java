package br.com.evandrorenan.infra.adapters.mappers;

import br.com.evandrorenan.infra.TestUtils;
import br.com.evandrorenan.infra.adapters.persistence.FlagDAO;
import br.com.evandrorenan.infra.adapters.rest.FlagDTO;
import br.com.featureflagsdkjava.domain.model.Flag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

@Slf4j
class FeatureFlagMapperTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldMapToDestinationObject() throws JsonProcessingException {
        FeatureFlagMapperImpl featureFlagMapper = new FeatureFlagMapperImpl();
        Flag baseFlag = createBaseFlag();
        FlagDAO flagDAO = featureFlagMapper.toFlagDAO(baseFlag);
        FlagDTO flagDTO = featureFlagMapper.toFlagDTO(baseFlag);
        Flag flagFromDAO = featureFlagMapper.toFlag(flagDAO);
        Flag flagFromDTO = featureFlagMapper.toFlag(flagDTO);

        List<FlagDAO> flagDAOList = List.of(flagDAO);
        List<FlagDTO> flagDTOList = List.of(flagDTO);
        List<Flag> flagsFromDAOList = featureFlagMapper.toFlagListFromDAO(flagDAOList);
        List<Flag> flagsFromDTOList = featureFlagMapper.toFlagListFromDto(flagDTOList);

        TestUtils.assertFieldsEqual(flagDAO, baseFlag);
        TestUtils.assertFieldsEqual(flagDTO, baseFlag);
        TestUtils.assertFieldsEqual(flagDAO, flagFromDAO);
        TestUtils.assertFieldsEqual(flagDTO, flagFromDTO);
        TestUtils.assertFieldsEqual(flagDAOList, flagsFromDAOList);
        TestUtils.assertFieldsEqual(flagDTOList, flagsFromDTOList);
    }

    private static Flag createBaseFlag() {
        Flag flag = new Flag();
        flag.setFlagName("test-flag");
        flag.setFlagType(Flag.FlagType.STRING);
        flag.setState(Flag.State.ENABLED);
        flag.setDefaultVariant("default-variant");
        Map<String, String> variants = Map.of(
                "default-variant", "DEFAULT",
                "alternative-variant", "ALTERNATIVE"
        );
        flag.setVariants(variants);
        return flag;
    }
}