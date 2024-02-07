package io.github.onecx.themes.bff.rs.controllers;

import java.io.InputStream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.theme.bff.clients.api.ImagesInternalApi;
import gen.io.github.onecx.theme.bff.clients.model.ImageInfo;
import gen.io.github.onecx.theme.bff.rs.internal.ImagesInternalApiService;
import gen.io.github.onecx.theme.bff.rs.internal.model.ImageInfoDTO;
import gen.io.github.onecx.theme.bff.rs.internal.model.ProblemDetailResponseDTO;
import io.github.onecx.themes.bff.rs.mappers.ExceptionMapper;
import io.github.onecx.themes.bff.rs.mappers.ImageMapper;

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

    @Override
    public Response getImage(String refId, String refType) {
        try (Response response = imageApi.getImage(refId, refType)) {
            return Response.status(Response.Status.OK).entity(response.getEntity()).build();
        }
    }

    @Override
    public Response updateImage(String refId, String refType, InputStream imageInputStream) {
        ImagesInternalApi.UpdateImageMultipartForm multipartForm = new ImagesInternalApi.UpdateImageMultipartForm();
        multipartForm.image = imageInputStream;
        try (Response response = imageApi.updateImage(multipartForm, refId, refType)) {
            ImageInfoDTO imageInfoDTO = imageMapper.map(response.readEntity(ImageInfo.class));
            return Response.status(response.getStatus()).entity(imageInfoDTO).build();
        }
    }

    @Override
    public Response uploadImage(String refId, String refType, InputStream imageInputStream) {
        ImagesInternalApi.UploadImageMultipartForm multipartForm = new ImagesInternalApi.UploadImageMultipartForm();
        multipartForm.image = imageInputStream;
        try (Response response = imageApi.uploadImage(multipartForm, refId, refType)) {
            ImageInfoDTO imageInfoDTO = imageMapper.map(response.readEntity(ImageInfo.class));
            return Response.status(response.getStatus()).entity(imageInfoDTO).build();
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
