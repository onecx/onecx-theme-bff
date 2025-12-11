package org.tkit.onecx.themes.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.File;
import java.util.List;
import java.util.Objects;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.tkit.onecx.themes.bff.rs.controllers.IconRestController;

import gen.org.tkit.onecx.theme.bff.clients.model.*;
import gen.org.tkit.onecx.theme.bff.rs.internal.model.*;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@TestHTTPEndpoint(IconRestController.class)
class IconRestControllerTest extends AbstractTest {

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private static final File FILE = new File(
            Objects.requireNonNull(ImageRestControllerTest.class.getResource("/iconsets/mdi-test-iconset.json")).getFile());

    @InjectMockServerClient
    MockServerClient mockServerClient;

    static final String MOCK_ID = "MOCK_ICON";

    @BeforeEach
    void resetExpectation() {
        try {
            mockServerClient.clear(MOCK_ID);
        } catch (Exception ex) {
            //  mockId not existing
        }
    }

    @Test
    void findIconsByNamesAndRefId_Test() {

        IconListResponse iconListResponse = new IconListResponse();
        Icon icon1 = new Icon();
        icon1.setName("prime:abc");
        icon1.setBody("someBody");
        icon1.setType("SVG");

        Icon icon2 = new Icon();
        icon2.setName("prime:def");
        icon2.setBody("someBody");
        icon2.setType("SVG");

        iconListResponse.setIcons(List.of(icon1, icon2));
        IconCriteria criteria = new IconCriteria();
        criteria.setNames(List.of("prime:abc", "prime:def"));

        mockServerClient.when(request().withPath("/internal/icons/theme1").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(criteria)))
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(iconListResponse)));

        IconCriteriaDTO criteriaDTO = new IconCriteriaDTO();
        criteriaDTO.setNames(List.of("prime:abc", "prime:def"));
        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(criteriaDTO)
                .pathParam("refId", "theme1")
                .post()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(IconListResponseDTO.class);

        Assertions.assertEquals(iconListResponse.getIcons().size(), output.getIcons().size());
        mockServerClient.clear("mock");
    }

    @Test
    void findIconsByNamesAndRefId_Missing_Criteria_Test() {

        IconCriteriaDTO criteriaDTO = new IconCriteriaDTO();
        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(criteriaDTO)
                .pathParam("refId", "theme1")
                .post()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void findIconsByNamesAndRefId_Client_Eception_Test() {
        IconCriteria criteria = new IconCriteria();
        criteria.setNames(List.of("prime:abc", "prime:def"));

        mockServerClient.when(request().withPath("/internal/icons/theme1").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(criteria)))
                .withId("mock")
                .respond(httpRequest -> response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode()));
        IconCriteriaDTO criteriaDTO = new IconCriteriaDTO();
        criteriaDTO.setNames(List.of("prime:abc", "prime:def"));
        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(criteriaDTO)
                .pathParam("refId", "theme1")
                .post()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        mockServerClient.clear("mock");
    }

    @Test
    void uploadIconSet() {

        var refId = "themeName";

        mockServerClient
                .when(request().withPath("/internal/icons/themeName/upload")
                        .withMethod(HttpMethod.POST))
                .withPriority(100)
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(OK.getStatusCode()));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .pathParam("refId", refId)
                .when()
                .body(FILE)
                .contentType(APPLICATION_JSON)
                .post("/upload")
                .then()
                .statusCode(OK.getStatusCode());
    }
}
