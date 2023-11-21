package io.github.onecx.themes.bff.rs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import gen.io.github.onecx.theme.bff.clients.api.ThemesInternalApi;
import gen.io.github.onecx.theme.bff.clients.model.ThemePageResult;
import gen.io.github.onecx.theme.bff.clients.model.ThemeSearchCriteria;
import gen.io.github.onecx.theme.bff.clients.model.UpdateTheme;
import gen.io.github.onecx.theme.bff.rs.internal.ThemesApiService;
import gen.io.github.onecx.theme.bff.rs.internal.model.*;
import io.github.onecx.themes.bff.rs.mappers.ResponseMapper;
import io.github.onecx.themes.bff.rs.mappers.ThemeMapper;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
public class ThemeRestController implements ThemesApiService {

    @Inject
    @RestClient
    ThemesInternalApi client;

    @Inject
    ThemeMapper mapper;

    @Inject
    ObjectMapper objectMapper;
    @Inject
    ResponseMapper responseMapper;

    @Override
    public Response createTheme(CreateThemeRequestDTO createThemeRequestDTO) {
        var createTheme = mapper.mapCreate(createThemeRequestDTO.getResource());
        Response response = client.createNewTheme(createTheme);
        ThemeDTO themeDTO = response.readEntity(ThemeDTO.class);
        CreateThemeResponseDTO createThemeResponseDTO = responseMapper.createThemeResponseDTOMapper(themeDTO);
        return Response.created(response.getLocation()).entity(createThemeResponseDTO).build();
    }

    @Override
    public Response deleteTheme(String id) {
        return Response.fromResponse(client.deleteTheme(id)).build();
    }

    @Override
    public Response getThemeById(String id) {
        Response response = client.getThemeById(id);
        ThemeDTO themeDTO = response.readEntity(ThemeDTO.class);
        GetThemeResponseDTO getThemeResponseDTO = responseMapper.getThemeResponseDTOMapper(themeDTO);
        return Response.ok(getThemeResponseDTO).build();
    }

    @Override
    public Response getThemes(Integer pageNumber, Integer pageSize) {
        Response response = client.getThemes(pageNumber, pageSize);
        ThemePageResult pageResult = response.readEntity(ThemePageResult.class);
        GetThemesResponseDTO getThemesResponseDTO = responseMapper.getThemesResponseMapper(pageResult);
        return Response.ok(getThemesResponseDTO).build();
    }

    @Override
    public Response searchThemes(SearchThemeRequestDTO searchThemeRequestDTO) {
        ThemeSearchCriteria searchCriteria = new ThemeSearchCriteria();
        searchCriteria.setName(searchThemeRequestDTO.getResource().getName());
        searchCriteria.setPageNumber(searchThemeRequestDTO.getPageNumber());
        searchCriteria.setPageSize(searchThemeRequestDTO.getPageSize());
        Response response = client.searchThemes(searchCriteria);
        ThemePageResult searchResult = response.readEntity(ThemePageResult.class);
        SearchThemeResponseDTO searchThemeResponseDTO = responseMapper.searchThemeResponseMapper(searchResult);
        return Response.ok(searchThemeResponseDTO).build();
    }

    @Override
    public Response updateTheme(String id, UpdateThemeRequestDTO updateThemeRequestDTO) {
        Response response = client.updateTheme(id, mapper.mapUpdate(updateThemeRequestDTO.getResource()));
        UpdateThemeResponseDTO updateThemeResponseDTO = responseMapper
                .updateThemeResponseMapper(response.readEntity(UpdateTheme.class));
        return Response.ok(updateThemeRequestDTO).build();
    }
}
