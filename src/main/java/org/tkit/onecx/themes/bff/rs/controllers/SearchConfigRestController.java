package org.tkit.onecx.themes.bff.rs.controllers;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

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
import org.tkit.onecx.themes.bff.rs.mappers.OnecxThemeBFFConfig;
import org.tkit.onecx.themes.bff.rs.mappers.SearchConfigMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.searchconfig.v1.client.api.SearchConfigApi;
import gen.org.tkit.onecx.searchconfig.v1.client.model.*;
import gen.org.tkit.onecx.theme.bff.rs.internal.SearchConfigApiService;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.*;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class SearchConfigRestController implements SearchConfigApiService {
    @Inject
    @RestClient
    SearchConfigApi searchConfigApi;

    @Inject
    OnecxThemeBFFConfig configOptions;

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    SearchConfigMapper mapper;

    @Override
    public Response createSearchConfig(CreateSearchConfigRequestDTO createSearchConfigRequestDTO) {
        if (!configOptions.searchconfig().searchConfigEnabled()) {
            return Response.status(BAD_REQUEST)
                    .entity(exceptionMapper.searchConfigDisabledException())
                    .build();
        }
        CreateSearchConfigRequest createSearchConfigRequest = mapper.map(createSearchConfigRequestDTO);
        createSearchConfigRequest.setAppId(configOptions.uiAppID());
        createSearchConfigRequest.setProductName(configOptions.productName());
        try (Response createResponse = searchConfigApi.createSearchConfig(createSearchConfigRequest)) {
            SearchConfigSearchRequest searchCriteria = new SearchConfigSearchRequest();
            searchCriteria.setPage(createSearchConfigRequest.getPage());
            searchCriteria.setAppId(configOptions.uiAppID());
            searchCriteria.setProductName(configOptions.productName());
            try (Response findAllResponse = searchConfigApi.findSearchConfigsBySearchCriteria(searchCriteria)) {
                CreateSearchConfigResponseDTO responseDTO = mapper
                        .mapCreate(findAllResponse.readEntity(SearchConfigSearchPageResult.class));
                return Response.status(createResponse.getStatus()).entity(responseDTO).build();
            }
        }
    }

    @Override
    public Response deleteSearchConfig(String configId) {
        if (!configOptions.searchconfig().searchConfigEnabled()) {
            return Response.status(BAD_REQUEST)
                    .entity(exceptionMapper.searchConfigDisabledException())
                    .build();
        }
        try (Response response = searchConfigApi.deleteSearchConfig(configId)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @Override
    public Response getSearchConfig(String configId) {
        if (!configOptions.searchconfig().searchConfigEnabled()) {
            return Response.status(BAD_REQUEST)
                    .entity(exceptionMapper.searchConfigDisabledException())
                    .build();
        }
        try (Response response = searchConfigApi.getConfigByConfigId(configId)) {
            return Response.status(response.getStatus()).entity(mapper.map(response.readEntity(SearchConfig.class))).build();
        }
    }

    @Override
    public Response getSearchConfigInfos(String page) {
        if (!configOptions.searchconfig().searchConfigEnabled()) {
            return Response.status(BAD_REQUEST)
                    .entity(exceptionMapper.searchConfigDisabledException())
                    .build();
        }
        SearchConfigSearchRequest searchCriteria = new SearchConfigSearchRequest();
        searchCriteria.setPage(page);
        searchCriteria.setAppId(configOptions.uiAppID());
        searchCriteria.setProductName(configOptions.productName());
        try (Response findAllResponse = searchConfigApi.findSearchConfigsBySearchCriteria(searchCriteria)) {
            GetSearchConfigInfosResponseDTO responseDTO = mapper
                    .map(findAllResponse.readEntity(SearchConfigSearchPageResult.class));
            return Response.status(findAllResponse.getStatus()).entity(responseDTO).build();
        }
    }

    @Override
    public Response updateSearchConfig(String configId, UpdateSearchConfigRequestDTO updateSearchConfigRequestDTO) {
        if (!configOptions.searchconfig().searchConfigEnabled()) {
            return Response.status(BAD_REQUEST)
                    .entity(exceptionMapper.searchConfigDisabledException())
                    .build();
        }
        UpdateSearchConfigRequest updatedSearchConfig = mapper.update(updateSearchConfigRequestDTO);
        updatedSearchConfig.setAppId(configOptions.uiAppID());
        updatedSearchConfig.setProductName(configOptions.productName());
        try (Response updateResponse = searchConfigApi.updateSearchConfig(configId, updatedSearchConfig)) {
            SearchConfigSearchRequest searchCriteria = new SearchConfigSearchRequest();
            searchCriteria.setPage(updatedSearchConfig.getPage());
            searchCriteria.setAppId(configOptions.uiAppID());
            searchCriteria.setProductName(configOptions.productName());
            try (Response findAllResponse = searchConfigApi.findSearchConfigsBySearchCriteria(searchCriteria)) {
                UpdateSearchConfigResponseDTO responseDTO = mapper
                        .mapUpdate(findAllResponse.readEntity(SearchConfigSearchPageResult.class));
                return Response.status(updateResponse.getStatus()).entity(responseDTO).build();
            }
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
