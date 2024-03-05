package org.tkit.onecx.themes.bff.rs.controllers;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.tkit.onecx.themes.bff.rs.mappers.ExceptionMapper;
import org.tkit.onecx.themes.bff.rs.mappers.SearchConfigOptions;

import gen.org.tkit.onecx.searchconfig.v1.client.api.SearchConfigApi;
import gen.org.tkit.onecx.theme.bff.rs.internal.SearchConfigApiService;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.CreateSearchConfigRequestDTO;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.UpdateSearchConfigRequestDTO;

public class SearchConfigRestController implements SearchConfigApiService {
    @Inject()
    @RestClient()
    SearchConfigApi searchConfigApi;

    @Inject()
    SearchConfigOptions configOptions;

    @Inject
    ExceptionMapper exceptionMapper;

    static final String CONFIG_DISABLED_ERROR_CODE = "ERROR_SEARCH_CONFIG_DISABLED";
    static final String CONFIG_DISABLED_ERROR_DETAIL = "The search config functionality is disabled for this BFF.";

    @Override
    public Response createSearchConfig(CreateSearchConfigRequestDTO createSearchConfigRequestDTO) {
        if (!configOptions.searchConfigEnabled()) {
            return Response.status(BAD_REQUEST)
                    .entity(
                            exceptionMapper.exception(CONFIG_DISABLED_ERROR_CODE, CONFIG_DISABLED_ERROR_DETAIL))
                    .build();
        }
        return null;
    }

    @Override
    public Response deleteSearchConfig(String configId) {
        if (!configOptions.searchConfigEnabled()) {
            return Response.status(BAD_REQUEST)
                    .entity(
                            exceptionMapper.exception(CONFIG_DISABLED_ERROR_CODE, CONFIG_DISABLED_ERROR_DETAIL))
                    .build();
        }
        return null;
    }

    @Override
    public Response getSearchConfigs(String page) {
        if (!configOptions.searchConfigEnabled()) {
            return Response.status(BAD_REQUEST)
                    .entity(
                            exceptionMapper.exception(CONFIG_DISABLED_ERROR_CODE, CONFIG_DISABLED_ERROR_DETAIL))
                    .build();
        }
        return null;
    }

    @Override
    public Response updateSearchConfig(String configId, UpdateSearchConfigRequestDTO updateSearchConfigRequestDTO) {
        if (!configOptions.searchConfigEnabled()) {
            return Response.status(BAD_REQUEST)
                    .entity(
                            exceptionMapper.exception(CONFIG_DISABLED_ERROR_CODE, CONFIG_DISABLED_ERROR_DETAIL))
                    .build();
        }
        // TODO Refetch list of all searchConfigs for current page and return list instead of only updated config
        return null;
    }
}
