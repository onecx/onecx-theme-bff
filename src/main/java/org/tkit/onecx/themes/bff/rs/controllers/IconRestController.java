package org.tkit.onecx.themes.bff.rs.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.themes.bff.rs.mappers.ExceptionMapper;
import org.tkit.onecx.themes.bff.rs.mappers.IconMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.theme.bff.clients.api.IconsInternalApi;
import gen.org.tkit.onecx.theme.bff.clients.model.IconListResponse;
import gen.org.tkit.onecx.theme.bff.rs.internal.IconsInternalApiService;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.IconCriteriaDTO;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class IconRestController implements IconsInternalApiService {

    @Inject
    @RestClient
    IconsInternalApi iconsApi;

    @Inject
    IconMapper iconMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Override
    public Response findIconsByNamesAndRefId(String refId, IconCriteriaDTO iconCriteriaDTO) {
        try (Response response = iconsApi.findIconsByNamesAndRefId(refId, iconMapper.mapCriteria(iconCriteriaDTO))) {

            var mappedIconList = iconMapper.map(response.readEntity(IconListResponse.class));
            return Response.status(response.getStatus()).entity(mappedIconList).build();
        }
    }

    @Override
    public Response uploadIconSet(String refId, byte[] body) {
        try (Response response = iconsApi.uploadIconSet(refId, body)) {
            return Response.status(response.getStatus()).build();
        }
    }

    @ServerExceptionMapper
    public Response restException(ClientWebApplicationException ex) {
        return exceptionMapper.clientException(ex);
    }
}
