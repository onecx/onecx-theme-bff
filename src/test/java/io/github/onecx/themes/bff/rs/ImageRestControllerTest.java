package io.github.onecx.themes.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.*;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.theme.bff.rs.internal.model.ImageInfoDTO;
import io.github.onecx.themes.bff.rs.controllers.ImageRestController;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@LogService
@TestHTTPEndpoint(ImageRestController.class)
public class ImageRestControllerTest {

    @InjectMockServerClient
    MockServerClient mockServerClient;

    @BeforeEach
    void resetMockServer() {

        mockServerClient.reset();
    }

    @Test
    void getImage() {

        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());
        String inputId = "11-111";

        mockServerClient.when(request()
                .withPath("/internal/images/" + inputId)
                .withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_OCTET_STREAM)
                        .withBody(String.valueOf(file)));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .get(inputId)
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void getImage_shouldReturnNotFound() {

        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());
        String inputId = "11-111";

        mockServerClient.when(request()
                .withPath("/internal/images/" + inputId)
                .withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .get(inputId)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void uploadImage() {
        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());

        ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
        imageInfoDTO.setId("11-111");

        mockServerClient.when(request().withPath("/internal/images").withMethod(HttpMethod.POST))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(imageInfoDTO)));

        var res = given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .when()
                .post()
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ImageInfoDTO.class);

        Assertions.assertNotNull(res);
        Assertions.assertEquals(res.getId(), imageInfoDTO.getId());

    }

    @Test
    void uploadImage_shouldReturnNotFound() {

        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());

        mockServerClient.when(request().withPath("/internal/images/").withMethod(HttpMethod.POST))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        var res = given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .when()
                .post()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
        Assertions.assertNotNull(res);
    }

    @Test
    void updateImage() {

        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());

        ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
        imageInfoDTO.setId("11-111");

        mockServerClient.when(request().withPath("/internal/images/" + imageInfoDTO.getId()).withMethod(HttpMethod.PUT))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(CREATED.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(imageInfoDTO)));

        var res = given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .when()
                .put(imageInfoDTO.getId())
                .then()
                .statusCode(CREATED.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ImageInfoDTO.class);

        Assertions.assertNotNull(res);
        Assertions.assertEquals(res.getId(), imageInfoDTO.getId());
    }

    @Test
    void updateImage_shouldReturnNotFound() {
        String inputId = "11-111";

        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());

        ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
        imageInfoDTO.setId("11-111");

        mockServerClient.when(request().withPath("/internal/images/" + inputId).withMethod(HttpMethod.PUT))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        var res = given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .when()
                .put(inputId)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
        Assertions.assertNotNull(res);
    }

}
