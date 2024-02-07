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
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@LogService
//@TestHTTPEndpoint(ImageRestController.class)
class ImageRestControllerTest {

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
        var refId = "themeName";
        var refType = "LOGO";

        mockServerClient.when(request()
                .withPath("/internal/images/" + refId + "/" + refType)
                .withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_OCTET_STREAM)
                        .withBody(String.valueOf(file)));

        given()
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .get("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void getImage_shouldReturnNotFound() {

        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());
        String inputId = "11-111";
        var refId = "themeName";
        var refType = "LOGO";

        mockServerClient.when(request()
                .withPath("/internal/images/" + refId + "/" + refType)
                .withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .get("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void uploadImage() {
        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());
        var refId = "themeName";
        var refType = "LOGO";

        ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
        imageInfoDTO.setId("11-111");

        mockServerClient.when(request().withPath("/internal/images/" + refId + "/" + refType).withMethod(HttpMethod.POST))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(imageInfoDTO)));

        var res = given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .post("/internal/images/{refId}/{refType}")
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
        var refId = "themeName";
        var refType = "LOGO";

        mockServerClient.when(request().withPath("/internal/images/" + refId + "/" + refType).withMethod(HttpMethod.POST))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        var res = given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .post("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
        Assertions.assertNotNull(res);
    }

    @Test
    void updateImage() {

        File file = new File(ImageRestControllerTest.class.getResource("/META-INF/resources/Testimage.png").getFile());
        var refId = "themeName";
        var refType = "LOGO";

        ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
        imageInfoDTO.setId("11-111");

        mockServerClient.when(request().withPath("/internal/images/" + refId + "/" + refType).withMethod(HttpMethod.PUT))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(CREATED.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(imageInfoDTO)));

        var res = given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .put("/internal/images/{refId}/{refType}")
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
        var refId = "themeName";
        var refType = "LOGO";

        ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
        imageInfoDTO.setId("11-111");

        mockServerClient.when(request().withPath("/internal/images/" + refId + "/" + refType).withMethod(HttpMethod.PUT))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        var res = given()
                .multiPart("image", file)
                .contentType("multipart/form-data")
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .when()
                .put("/internal/images/{refId}/{refType}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
        Assertions.assertNotNull(res);
    }

}
