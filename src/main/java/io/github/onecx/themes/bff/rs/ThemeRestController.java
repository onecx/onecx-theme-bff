package io.github.onecx.themes.bff.rs;

import gen.io.github.onecx.theme.bff.rs.internal.ThemesApiService;
import gen.io.github.onecx.theme.bff.rs.internal.model.CreateThemeRequestDTO;
import gen.io.github.onecx.theme.bff.rs.internal.model.SearchThemeRequestDTO;
import gen.io.github.onecx.theme.bff.rs.internal.model.UpdateThemeRequestDTO;
import io.github.onecx.themes.bff.rs.mappers.ThemeMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import gen.io.github.onecx.theme.bff.clients.api.ThemesInternalApi;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
public class ThemeRestController implements ThemesApiService {

  @Inject
  @RestClient
  ThemesInternalApi client;

  @Inject
  ThemeMapper mapper;

  @Override
  public Response createTheme(CreateThemeRequestDTO createThemeRequestDTO) {
    var dto = mapper.map(createThemeRequestDTO);
    return Response.fromResponse(client.createNewTheme(dto)).build();  }

  @Override
  public Response deleteTheme(String id) {
    return Response.fromResponse(client.deleteTheme(id)).build();
  }

  @Override
  public Response getThemeById(String id) {
    return Response.fromResponse(client.getThemeById(id)).build();
  }

  @Override
  public Response getThemes(Integer pageNumber, Integer pageSize) {
    return Response.fromResponse(client.getThemes(pageNumber, pageSize)).build();
  }

  @Override
  public Response searchThemes(SearchThemeRequestDTO searchThemeRequestDTO) {
    var searchCriteria = mapper.mapSearch(searchThemeRequestDTO.getResource());
    return Response.fromResponse(client.searchThemes(searchCriteria)).build();
  }

  @Override
  public Response updateTheme(String id, UpdateThemeRequestDTO updateThemeRequestDTO) {
    var dto = mapper.mapUpdate(updateThemeRequestDTO);
    return Response.fromResponse(client.updateTheme(id, dto)).build();
  }
}
