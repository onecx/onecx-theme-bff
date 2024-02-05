package org.tkit.onecx.themes.bff.rs.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.themes.bff.rs.mappers.ExceptionMapper;
import org.tkit.onecx.themes.bff.rs.mappers.ProblemDetailMapper;
import org.tkit.onecx.themes.bff.rs.mappers.ThemeMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.theme.bff.clients.api.ThemesExportImportApi;
import gen.org.tkit.onecx.theme.bff.clients.api.ThemesInternalApi;
import gen.org.tkit.onecx.theme.bff.clients.api.WorkspaceExternalApi;
import gen.org.tkit.onecx.theme.bff.clients.model.*;
import gen.org.tkit.onecx.theme.bff.rs.internal.ThemesApiService;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.*;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class ThemeRestController implements ThemesApiService {
    @Inject
    @RestClient
    ThemesInternalApi client;

    @Inject
    @RestClient
    WorkspaceExternalApi workspaceClient;

    @Inject
    @RestClient
    ThemesExportImportApi eximClient;

    @Inject
    ThemeMapper mapper;

    @Inject
    ProblemDetailMapper problemDetailMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response createTheme(CreateThemeRequestDTO createThemeRequestDTO) {
        try (Response response = client.createNewTheme(mapper.mapCreate(createThemeRequestDTO))) {
            CreateThemeResponseDTO createThemeResponseDTO = mapper
                    .createThemeResponseDTOMapper(response.readEntity(ThemeDTO.class));
            return Response.status(response.getStatus()).entity(createThemeResponseDTO).build();
        } catch (WebApplicationException ex) {
            return Response.status(ex.getResponse().getStatus())
                    .entity(problemDetailMapper.map(ex.getResponse().readEntity(ProblemDetailResponse.class))).build();
        }
    }

    @Override
    public Response deleteTheme(String id) {
        try (Response response = client.deleteTheme(id)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response exportThemes(ExportThemeRequestDTO exportThemeRequestDTO) {
        try (Response response = eximClient.exportThemes(mapper.map(exportThemeRequestDTO))) {
            return Response.status(response.getStatus()).entity(mapper.map(response.readEntity(ThemeSnapshot.class))).build();
        }
    }

    @Override
    public Response getThemeById(String id) {
        try (Response response = client.getThemeById(id)) {
            Theme theme = response.readEntity(Theme.class);
            WorkspacePageResult workspaces = null;
            try (Response workspaceResponse = workspaceClient.searchWorkspaces(mapper.map(theme.getName()))) {
                workspaces = workspaceResponse.readEntity(WorkspacePageResult.class);
            } catch (WebApplicationException ex) {
            }
            GetThemeResponseDTO getThemeResponseDTO = mapper.getThemeResponseDTOMapper(theme, workspaces);
            return Response.status(response.getStatus()).entity(getThemeResponseDTO).build();
        }
    }

    @Override
    public Response getThemeByName(String name) {
        try (Response response = client.getThemeByThemeDefinitionName(name)) {
            Theme theme = response.readEntity(Theme.class);
            WorkspacePageResult workspaces = null;
            try (Response workspaceResponse = workspaceClient.searchWorkspaces(mapper.map(theme.getName()))) {
                workspaces = workspaceResponse.readEntity(WorkspacePageResult.class);
            } catch (WebApplicationException ex) {
            }
            GetThemeResponseDTO getThemeResponseDTO = mapper.getThemeResponseDTOMapper(theme, workspaces);
            return Response.status(response.getStatus()).entity(getThemeResponseDTO).build();
        }
    }

    @Override
    public Response getThemes(Integer pageNumber, Integer pageSize) {
        try (Response response = client.getThemes(pageNumber, pageSize)) {
            GetThemesResponseDTO getThemesResponseDTO = mapper
                    .getThemesResponseMapper(response.readEntity(ThemePageResult.class));
            return Response.status(response.getStatus()).entity(getThemesResponseDTO).build();
        }
    }

    @Override
    public Response importThemes(ThemeSnapshotDTO themeSnapshotDTO) {
        try (Response response = eximClient.importThemes(mapper.map(themeSnapshotDTO))) {
            return Response.status(response.getStatus())
                    .entity(mapper.map(response.readEntity(ImportThemeResponse.class))).build();
        }
    }

    @Override
    public Response searchThemes(SearchThemeRequestDTO searchThemeRequestDTO) {
        try (Response response = client.searchThemes(mapper.mapSearchCriteria(searchThemeRequestDTO))) {
            SearchThemeResponseDTO searchThemeResponseDTO = mapper
                    .searchThemeResponseMapper(response.readEntity(ThemePageResult.class));
            return Response.status(response.getStatus()).entity(searchThemeResponseDTO).build();
        }
    }

    @Override
    public Response updateTheme(String id, UpdateThemeRequestDTO updateThemeRequestDTO) {
        try (Response response = client.updateTheme(id, mapper.mapUpdate(updateThemeRequestDTO))) {
            try (Response updatedThemeResponse = client.getThemeById(id)) {
                UpdateThemeResponseDTO updateThemeResponseDTO = mapper.map(updatedThemeResponse.readEntity(Theme.class));
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
