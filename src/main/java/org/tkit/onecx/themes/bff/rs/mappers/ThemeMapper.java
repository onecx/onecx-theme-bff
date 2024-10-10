package org.tkit.onecx.themes.bff.rs.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.theme.bff.rs.internal.model.*;
import gen.org.tkit.onecx.theme.client.model.*;
import gen.org.tkit.onecx.theme.exim.client.model.EximTheme;
import gen.org.tkit.onecx.theme.exim.client.model.ExportThemeRequest;
import gen.org.tkit.onecx.theme.exim.client.model.ImportThemeResponse;
import gen.org.tkit.onecx.theme.exim.client.model.ThemeSnapshot;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ThemeMapper {

    @Mapping(source = "resource", target = ".")
    UpdateTheme mapUpdate(UpdateThemeRequestDTO dto);

    @Mapping(source = "resource", target = ".")
    CreateTheme mapCreate(CreateThemeRequestDTO dto);

    @Mapping(source = ".", target = "resource")
    UpdateThemeResponseDTO map(Theme theme);

    @Mapping(source = "theme", target = "resource")
    GetThemeResponseDTO getThemeResponseDTOMapper(Theme theme);

    @Mapping(source = ".", target = "resource")
    CreateThemeResponseDTO createThemeResponseDTOMapper(ThemeDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    //    @Mapping(target = "stream.version", ignore = true)
    GetThemesResponseDTO getThemesResponseMapper(ThemePageResult pageResult);

    @Mapping(target = "removeStreamItem", ignore = true)
    SearchThemeResponseDTO searchThemeResponseMapper(ThemePageResult pageResult);

    ThemeSearchCriteria mapSearchCriteria(SearchThemeRequestDTO searchThemeRequestDTO);

    ExportThemeRequest map(ExportThemeRequestDTO exportThemeRequestDTO);

    @Mapping(target = "removeThemesItem", ignore = true)
    ThemeSnapshotDTO map(ThemeSnapshot themeSnapshot);

    ThemeSnapshot map(ThemeSnapshotDTO themeSnapshot);

    @Mapping(target = "removeImagesItem", ignore = true)
    EximThemeDTO map(EximTheme eximTheme);

    @Mapping(target = "removeThemesItem", ignore = true)
    ImportThemeResponseDTO map(ImportThemeResponse response);

}
