package br.com.evandrorenan.infra.adapters.mappers;

import br.com.evandrorenan.infra.adapters.persistence.FlagDAO;
import br.com.evandrorenan.infra.adapters.rest.FlagDTO;
import br.com.featureflagsdkjava.domain.model.Flag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FeatureFlagMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "defaultVariant", target = "defaultVariant")
    @Mapping(source = "targeting", target = "targeting")
    @Mapping(source = "variants", target = "variants")
    Flag toFlag(FlagDAO flagDAO);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "defaultVariant", target = "defaultVariant")
    @Mapping(source = "targeting", target = "targeting")
    @Mapping(source = "variants", target = "variants")
    Flag toFlag(FlagDTO flagDTO);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "defaultVariant", target = "defaultVariant")
    @Mapping(source = "targeting", target = "targeting")
    @Mapping(source = "variants", target = "variants")
    FlagDAO toFlagDAO(Flag flag);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "defaultVariant", target = "defaultVariant")
    @Mapping(source = "targeting", target = "targeting")
    @Mapping(source = "variants", target = "variants")
    FlagDTO toFlagDTO(Flag flag);

    List<Flag> toFlagListFromDAO(List<FlagDAO> flagDAOS);

    List<Flag> toFlagListFromDto(List<FlagDTO> flagDTOS);
}
