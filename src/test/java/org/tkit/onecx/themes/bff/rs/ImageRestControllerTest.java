package org.tkit.onecx.themes.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.*;
import java.util.Objects;
import java.util.Random;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.tkit.onecx.themes.bff.rs.controllers.ImageRestController;

import gen.org.tkit.onecx.theme.bff.clients.model.MimeType;
import gen.org.tkit.onecx.theme.bff.clients.model.ProblemDetailResponse;
import gen.org.tkit.onecx.theme.bff.clients.model.RefType;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.ImageInfoDTO;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.MimeTypeDTO;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.RefTypeDTO;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@TestHTTPEndpoint(ImageRestController.class)
class ImageRestControllerTest extends AbstractTest {

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private static final String MEDIA_TYPE_IMAGE_PNG = "image/png";
    private static final String MEDIA_TYPE_IMAGE_JPG = "image/jpg";

    private static final File FILE = new File(
            Objects.requireNonNull(ImageRestControllerTest.class.getResource("/images/Testimage.png")).getFile());

    @InjectMockServerClient
    MockServerClient mockServerClient;

    static final String MOCK_ID = "MOCK_IMAGE";

    @BeforeEach
    void resetExpectation() {
        try {
            mockServerClient.clear(MOCK_ID);
        } catch (Exception ex) {
            //  mockId not existing
        }
    }

    @Test
    void getImagePNG() {

        var refId = "themeName";
        var refType = RefTypeDTO.FAVICON;
        byte[] bytesRes = new byte[] { (byte) 0xe0, 0x4f, (byte) 0xd0,
                0x20, (byte) 0xea, 0x3a, 0x69, 0x10, (byte) 0xa2, (byte) 0xd8, 0x08, 0x00, 0x2b,
                0x30, 0x30, (byte) 0x9d };

        mockServerClient.when(request()
                .withPath("/internal/images/" + refId + "/" + RefType.FAVICON)
                .withMethod(HttpMethod.GET))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withHeaders(
                                new Header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_IMAGE_PNG))
                        .withBody(bytesRes));

        var res = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_IMAGE_PNG)
                .extract().body().asByteArray();

        assertThat(res).isNotNull().isNotEmpty();
    }

    @Test
    void getImageJPG() {

        var refId = "themeName";
        var refType = RefTypeDTO.FAVICON;
        byte[] bytesRes = new byte[] { (byte) 0xe0, 0x4f, (byte) 0xd0,
                0x20, (byte) 0xea, 0x3a, 0x69, 0x10, (byte) 0xa2, (byte) 0xd8, 0x08, 0x00, 0x2b,
                0x30, 0x30, (byte) 0x9d };

        mockServerClient.when(request()
                .withPath("/internal/images/" + refId + "/" + RefType.FAVICON)
                .withMethod(HttpMethod.GET))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withHeaders(
                                new Header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_IMAGE_JPG))
                        .withBody(bytesRes));

        var res = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get()
                .then()
                .statusCode(OK.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_IMAGE_JPG)
                .extract().body().asByteArray();

        assertThat(res).isNotNull().isNotEmpty();
    }

    @Test
    void getImage_shouldReturnBadRequest() {

        var refId = "themeName";
        ProblemDetailResponse problemDetailResponse = new ProblemDetailResponse();
        problemDetailResponse.setErrorCode(String.valueOf(BAD_REQUEST));
        problemDetailResponse.setDetail("uploadImage.contentLength: must be less than or equal to 110000");

        mockServerClient.when(request()
                .withPath("/internal/images/" + refId + "/" + RefType.LOGO)
                .withMethod(HttpMethod.GET))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(BAD_REQUEST.getStatusCode())
                        .withContentType(MediaType.ANY_IMAGE_TYPE)
                        .withBody(JsonBody.json(problemDetailResponse)));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", RefTypeDTO.LOGO)
                .get()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void getImage_shouldReturnBadRequest_whenBodyEmpty() {

        var refId = "themeName";
        var refType = RefTypeDTO.FAVICON;
        byte[] bytesRes = null;

        mockServerClient.when(request()
                .withPath("/internal/images/" + refId + "/" + RefType.FAVICON)
                .withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withHeaders(
                                new Header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_IMAGE_JPG))
                        .withBody(bytesRes));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

    }

    @Test
    void getImage_shouldReturnBadRequest_whenContentTypeEmpty() {

        var refId = "themeName";
        var refType = RefTypeDTO.FAVICON;
        byte[] bytesRes = new byte[] { (byte) 0xe0, 0x4f, (byte) 0xd0,
                0x20, (byte) 0xea, 0x3a, 0x69, 0x10, (byte) 0xa2, (byte) 0xd8, 0x08, 0x00, 0x2b,
                0x30, 0x30, (byte) 0x9d };

        mockServerClient.when(request()
                .withPath("/internal/images/" + refId + "/" + RefType.FAVICON)
                .withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withBody(bytesRes));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

    }

    @Test
    void getImage_shouldReturnBadRequest_whenAllEmpty() {

        var refId = "themeName";
        var refType = RefTypeDTO.FAVICON;

        mockServerClient.when(request()
                .withPath("/internal/images/" + refId + "/" + RefType.FAVICON)
                .withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("refId", refId)
                .pathParam("refType", refType)
                .get()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

    }

    @Test
    void uploadImage() {

        var refId = "themeName";

        ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
        imageInfoDTO.setId("11-111");

        mockServerClient
                .when(request().withPath("/internal/images/" + refId + "/" + RefType.LOGO)
                        .withMethod(HttpMethod.POST)
                        .withHeader("mimeType", String.valueOf(MimeType.IMAGE_SLASH_PNG)))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(imageInfoDTO)));

        var res = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_IMAGE_PNG)
                .pathParam("refId", refId)
                .pathParam("refType", RefTypeDTO.LOGO)
                .header("mimeType", MimeTypeDTO.IMAGE_PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
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
        var refId = "themeName";

        mockServerClient
                .when(request().withPath("/internal/images/" + refId + "/" + RefType.LOGO)
                        .withMethod(HttpMethod.POST)
                        .withHeader("mimeType", String.valueOf(MimeType.IMAGE_SLASH_PNG)))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        var res = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .pathParam("refId", refId)
                .pathParam("refType", RefTypeDTO.LOGO)
                .header("mimeType", MimeTypeDTO.IMAGE_PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .when()
                .post()
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
        Assertions.assertNotNull(res);
    }

    @Test
    void updateImage() {

        var refId = "themeName";

        ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
        imageInfoDTO.setId("11-111");

        mockServerClient
                .when(request().withPath("/internal/images/" + refId + "/" + RefType.LOGO)
                        .withMethod(HttpMethod.POST)
                        .withHeader("mimeType", String.valueOf(MimeType.IMAGE_SLASH_PNG)))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(CREATED.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(imageInfoDTO)));

        var res = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .pathParam("refId", refId)
                .pathParam("refType", RefTypeDTO.LOGO)
                .header("mimeType", MimeTypeDTO.IMAGE_PNG)
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ImageInfoDTO.class);

        Assertions.assertNotNull(res);
        Assertions.assertEquals(res.getId(), imageInfoDTO.getId());
    }

    @Test
    void deleteImage() {
        var refId = "workspaceName";
        mockServerClient
                .when(request().withPath("/internal/images/" + refId + "/" + RefType.LOGO).withMethod(HttpMethod.DELETE))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(NO_CONTENT.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .pathParam("refId", refId)
                .pathParam("refType", RefTypeDTO.LOGO)
                .when()
                .delete()
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testMaxUploadSize() {

        var refId = "themeMaxUpload";

        byte[] body = new byte[110001];
        new Random().nextBytes(body);
        ProblemDetailResponse problemDetailResponse = new ProblemDetailResponse();
        problemDetailResponse.setErrorCode("CONSTRAINT_VIOLATIONS");
        problemDetailResponse.setDetail("uploadImage.contentLength: must be less than or equal to 110000");

        mockServerClient
                .when(request().withPath("/internal/images/" + refId + "/" + RefType.LOGO)
                        .withMethod(HttpMethod.POST)
                        .withHeader("mimeType", String.valueOf(MimeType.IMAGE_SLASH_PNG)))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(BAD_REQUEST.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(problemDetailResponse)));

        var exception = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .pathParam("refId", refId)
                .pathParam("refType", RefTypeDTO.LOGO)
                .header("mimeType", MimeTypeDTO.IMAGE_PNG)
                .when()
                .body(body)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .contentType(APPLICATION_JSON)
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo(
                "uploadImage.contentLength: must be less than or equal to 110000");

    }
}
