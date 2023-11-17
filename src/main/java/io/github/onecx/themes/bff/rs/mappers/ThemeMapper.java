package io.github.onecx.themes.bff.rs.mappers;

import gen.io.github.onecx.theme.bff.clients.model.CreateTheme;
import gen.io.github.onecx.theme.bff.clients.model.ThemeSearchCriteria;
import gen.io.github.onecx.theme.bff.clients.model.UpdateTheme;
import gen.io.github.onecx.theme.bff.rs.internal.model.CreateThemeRequestDTO;
import gen.io.github.onecx.theme.bff.rs.internal.model.ThemeDTO;
import gen.io.github.onecx.theme.bff.rs.internal.model.UpdateThemeRequestDTO;
import org.mapstruct.Mapper;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ThemeMapper {
  ThemeSearchCriteria mapSearch(ThemeDTO dto);
  UpdateTheme mapUpdate(UpdateThemeRequestDTO dto);
  CreateTheme map(CreateThemeRequestDTO dto);


}