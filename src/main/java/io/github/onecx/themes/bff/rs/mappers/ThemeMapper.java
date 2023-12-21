package io.github.onecx.themes.bff.rs.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.io.github.onecx.theme.bff.clients.model.*;
import gen.io.github.onecx.theme.bff.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ThemeMapper {

    @Mapping(source = "resource", target = ".")
    UpdateTheme mapUpdate(UpdateThemeRequestDTO dto);

    @Mapping(source = "resource", target = ".")
    CreateTheme mapCreate(CreateThemeRequestDTO dto);

    @Mapping(source = ".", target = "resource")
    UpdateThemeResponseDTO map(Theme theme);

    @Mapping(source = ".", target = "resource")
    @Mapping(target = "removePortalsItem", ignore = true)
    @Mapping(target = "portals", ignore = true)
    //remove after workspace api is implemented
    GetThemeResponseDTO getThemeResponseDTOMapper(Theme theme);

    @Mapping(source = ".", target = "resource")
    CreateThemeResponseDTO createThemeResponseDTOMapper(ThemeDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    GetThemesResponseDTO getThemesResponseMapper(ThemePageResult pageResult);

    @Mapping(target = "removeStreamItem", ignore = true)
    SearchThemeResponseDTO searchThemeResponseMapper(ThemePageResult pageResult);

    @Mapping(source = "resource", target = ".")
    ThemeSearchCriteria mapSearchCriteria(SearchThemeRequestDTO searchThemeRequestDTO);
}
