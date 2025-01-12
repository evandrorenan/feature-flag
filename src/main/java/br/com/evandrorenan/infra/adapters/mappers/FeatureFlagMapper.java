package br.com.evandrorenan.infra.adapters.mappers;

import br.com.evandrorenan.infra.adapters.persistence.FlagDAO;
import br.com.evandrorenan.infra.adapters.rest.FlagDTO;
import br.com.featureflagsdkjava.domain.model.Flag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FeatureFlagMapper {
    @Mapping(source = "flagName", target = "flagName")
    @Mapping(source = "flagType", target = "flagType")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "defaultVariant", target = "defaultVariant")
    @Mapping(source = "targeting", target = "targeting")
    @Mapping(source = "variants", target = "variants")
    Flag toFlag(FlagDAO flagDAO);

    @Mapping(source = "flagName", target = "flagName")
    @Mapping(source = "flagType", target = "flagType")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "defaultVariant", target = "defaultVariant")
    @Mapping(source = "targeting", target = "targeting")
    @Mapping(source = "variants", target = "variants")
    Flag toFlag(FlagDTO flagDTO);

    @Mapping(source = "flagName", target = "flagName")
    @Mapping(source = "flagType", target = "flagType")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "defaultVariant", target = "defaultVariant")
    @Mapping(source = "targeting", target = "targeting")
    @Mapping(source = "variants", target = "variants")
    FlagDAO toFlagDAO(Flag flag);

    @Mapping(source = "flagName", target = "flagName")
    @Mapping(source = "flagType", target = "flagType")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "defaultVariant", target = "defaultVariant")
    @Mapping(source = "targeting", target = "targeting")
    @Mapping(source = "variants", target = "variants")
    FlagDTO toFlagDTO(Flag flag);

    List<Flag> toFlagListFromDAO(List<FlagDAO> flagDAOS);

    List<Flag> toFlagListFromDto(List<FlagDTO> flagDTOS);
}
