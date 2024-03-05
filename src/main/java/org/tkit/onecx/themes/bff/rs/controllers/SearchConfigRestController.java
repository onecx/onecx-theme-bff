package org.tkit.onecx.themes.bff.rs.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import gen.org.tkit.onecx.searchconfig.v1.client.api.SearchConfigApi;
import gen.org.tkit.onecx.theme.bff.rs.internal.SearchConfigApiService;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.CreateSearchConfigRequestDTO;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.UpdateSearchConfigRequestDTO;

public class SearchConfigRestController implements SearchConfigApiService {
    @Inject()
    @RestClient()
    SearchConfigApi searchConfigApi;

    @Override
    public Response createSearchConfig(CreateSearchConfigRequestDTO createSearchConfigRequestDTO) {
        return null;
    }

    @Override
    public Response deleteSearchConfig(String configId) {
        return null;
    }

    @Override
    public Response getSearchConfigs(String page) {
        return null;
    }

    @Override
    public Response updateSearchConfig(String configId, UpdateSearchConfigRequestDTO updateSearchConfigRequestDTO) {
        // TODO Refetch list of all searchConfigs for current page and return list instead of only updated config
        return null;
    }
}
