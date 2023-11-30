package io.github.onecx.themes.bff.rs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.theme.bff.clients.api.ThemesInternalApi;
import gen.io.github.onecx.theme.bff.clients.model.*;
import gen.io.github.onecx.theme.bff.rs.internal.ThemesApiService;
import gen.io.github.onecx.theme.bff.rs.internal.model.*;
import io.github.onecx.themes.bff.rs.mappers.ExceptionMapper;
import io.github.onecx.themes.bff.rs.mappers.ProblemDetailMapper;
import io.github.onecx.themes.bff.rs.mappers.ResponseMapper;
import io.github.onecx.themes.bff.rs.mappers.ThemeMapper;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
@Path("/themes")
public class ThemeRestController implements ThemesApiService {
    @Inject
    @RestClient
    ThemesInternalApi client;

    @Inject
    ThemeMapper mapper;

    @Inject
    ResponseMapper responseMapper;

    @Inject
    ProblemDetailMapper problemDetailMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response createTheme(CreateThemeRequestDTO createThemeRequestDTO) {
        try (Response response = client.createNewTheme(mapper.mapCreate(createThemeRequestDTO.getResource()))) {
            ThemeDTO themeDTO = response.readEntity(ThemeDTO.class);
            CreateThemeResponseDTO createThemeResponseDTO = responseMapper.createThemeResponseDTOMapper(themeDTO);
            return Response.status(response.getStatus()).entity(createThemeResponseDTO).build();
        } catch (WebApplicationException ex) {
            return Response.status(ex.getResponse().getStatus())
                    .entity(problemDetailMapper.map(ex.getResponse().readEntity(ProblemDetailResponse.class))).build();
        }
    }

    @Override
    public Response deleteTheme(String id) {
        try (Response response = client.deleteTheme(id)) {
            return Response.fromResponse(client.deleteTheme(id)).build();
        }
    }

    @Override
    public Response getThemeById(String id) {
        try (Response response = client.getThemeById(id)) {
            ThemeDTO themeDTO = mapper.map(response.readEntity(Theme.class));
            GetThemeResponseDTO getThemeResponseDTO = responseMapper.getThemeResponseDTOMapper(themeDTO);
            return Response.ok(getThemeResponseDTO).build();
        }
    }

    @Override
    public Response getThemes(Integer pageNumber, Integer pageSize) {
        try (Response response = client.getThemes(pageNumber, pageSize)) {
            ThemePageResult pageResult = response.readEntity(ThemePageResult.class);
            GetThemesResponseDTO getThemesResponseDTO = responseMapper.getThemesResponseMapper(pageResult);
            return Response.ok(getThemesResponseDTO).build();
        }
    }

    @Override
    public Response searchThemes(SearchThemeRequestDTO searchThemeRequestDTO) {
        ThemeSearchCriteria searchCriteria = new ThemeSearchCriteria();
        searchCriteria.setName(searchThemeRequestDTO.getResource().getName());
        searchCriteria.setPageNumber(searchThemeRequestDTO.getPageNumber());
        searchCriteria.setPageSize(searchThemeRequestDTO.getPageSize());
        try (Response response = client.searchThemes(searchCriteria)) {
            ThemePageResult searchResult = response.readEntity(ThemePageResult.class);
            SearchThemeResponseDTO searchThemeResponseDTO = responseMapper.searchThemeResponseMapper(searchResult);
            return Response.ok(searchThemeResponseDTO).build();
        }
    }

    @Override
    public Response updateTheme(String id, UpdateThemeRequestDTO updateThemeRequestDTO) {
        try (Response response = client.updateTheme(id, mapper.mapUpdate(updateThemeRequestDTO.getResource()))) {
            try (Response updatedThemeResponse = client.getThemeById(id)) {
                UpdateThemeResponseDTO updateThemeResponseDTO = new UpdateThemeResponseDTO();
                updateThemeResponseDTO.setResource(mapper.map(updatedThemeResponse.readEntity(Theme.class)));
                return Response.ok(updateThemeResponseDTO).build();
            }
        } catch (WebApplicationException ex) {
            return Response.status(ex.getResponse().getStatus())
                    .entity(problemDetailMapper.map(ex.getResponse().readEntity(ProblemDetailResponse.class))).build();
        }
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public Response restException(WebApplicationException ex) {
        return Response.status(ex.getResponse().getStatus()).build();
    }
}
