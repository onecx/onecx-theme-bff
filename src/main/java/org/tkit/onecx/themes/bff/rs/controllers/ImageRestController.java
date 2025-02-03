package org.tkit.onecx.themes.bff.rs.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.themes.bff.rs.mappers.ExceptionMapper;
import org.tkit.onecx.themes.bff.rs.mappers.ImageMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.theme.bff.clients.api.ImagesInternalApi;
import gen.org.tkit.onecx.theme.bff.clients.model.ImageInfo;
import gen.org.tkit.onecx.theme.bff.rs.internal.ImagesInternalApiService;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.ImageInfoDTO;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.MimeTypeDTO;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.RefTypeDTO;

@ApplicationScoped
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)
@LogService
public class ImageRestController implements ImagesInternalApiService {

    @Inject
    @RestClient
    ImagesInternalApi imageApi;

    @Inject
    ImageMapper imageMapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    HttpHeaders headers;

    @Override
    public Response getImage(String refId, RefTypeDTO refType) {
        Response.ResponseBuilder responseBuilder;
        try (Response response = imageApi.getImage(refId, imageMapper.map(refType))) {
            var contentType = response.getHeaderString(HttpHeaders.CONTENT_TYPE);
            var contentLength = response.getHeaderString(HttpHeaders.CONTENT_LENGTH);
            var body = response.readEntity(byte[].class);
            if (contentType != null && body.length != 0) {
                responseBuilder = Response.status(response.getStatus())
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .header(HttpHeaders.CONTENT_LENGTH, contentLength)
                        .entity(body);
            } else {
                responseBuilder = Response.status(Response.Status.BAD_REQUEST);
            }

            return responseBuilder.build();
        }
    }

    @Override
    public Response updateImage(String refId, RefTypeDTO refType, MimeTypeDTO mimeType, byte[] body) {

        try (Response response = imageApi.updateImage(refId, imageMapper.map(refType), imageMapper.mapMimeType(mimeType), body,
                headers.getLength())) {

            ImageInfoDTO imageInfoDTO = imageMapper.map(response.readEntity(ImageInfo.class));
            return Response.status(response.getStatus()).entity(imageInfoDTO).build();
        }
    }

    @Override
    public Response uploadImage(MimeTypeDTO mimeType, String refId, RefTypeDTO refType, byte[] body) {

        try (Response response = imageApi.uploadImage(headers.getLength(), refId, imageMapper.map(refType),
                imageMapper.mapMimeType(mimeType), body)) {
            ImageInfoDTO imageInfoDTO = imageMapper.map(response.readEntity(ImageInfo.class));
            return Response.status(response.getStatus()).entity(imageInfoDTO).build();
        }
    }

    @ServerExceptionMapper
    public Response restException(ClientWebApplicationException ex) {
        return exceptionMapper.clientException(ex);
    }
}
