package org.tkit.onecx.themes.bff.rs.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.theme.bff.clients.model.*;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.*;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ThemeMapper {

    @Mapping(source = "resource", target = ".")
    UpdateTheme mapUpdate(UpdateThemeRequestDTO dto);

    @Mapping(source = "resource", target = ".")
    CreateTheme mapCreate(CreateThemeRequestDTO dto);

    @Mapping(source = ".", target = "resource")
    UpdateThemeResponseDTO map(Theme theme);

    @Mapping(source = "theme", target = "resource")
    @Mapping(source = "workspacePageResult.stream", target = "workspaces")
    @Mapping(target = "removeWorkspacesItem", ignore = true)
    GetThemeResponseDTO getThemeResponseDTOMapper(Theme theme, WorkspacePageResult workspacePageResult);

    @Mapping(source = ".", target = "resource")
    CreateThemeResponseDTO createThemeResponseDTOMapper(ThemeDTO dto);

    @Mapping(target = "removeStreamItem", ignore = true)
    GetThemesResponseDTO getThemesResponseMapper(ThemePageResult pageResult);

    @Mapping(target = "removeStreamItem", ignore = true)
    SearchThemeResponseDTO searchThemeResponseMapper(ThemePageResult pageResult);

    ThemeSearchCriteria mapSearchCriteria(SearchThemeRequestDTO searchThemeRequestDTO);

    ExportThemeRequest map(ExportThemeRequestDTO exportThemeRequestDTO);

    @Mapping(target = "removeThemesItem", ignore = true)
    ThemeSnapshotDTO map(ThemeSnapshot themeSnapshot);

    ThemeSnapshot map(ThemeSnapshotDTO themeSnapshot);

    @Mapping(target = "removeThemesItem", ignore = true)
    ImportThemeResponseDTO map(ImportThemeResponse response);

    @Mapping(target = "themeName", source = "themeName")
    @Mapping(target = "pageNumber", ignore = true)
    @Mapping(target = "pageSize", ignore = true)
    WorkspaceSearchCriteria map(String themeName);
}
