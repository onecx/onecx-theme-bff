package org.tkit.onecx.themes.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.*;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.tkit.onecx.themes.bff.rs.controllers.ThemeRestController;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.theme.bff.rs.internal.model.*;
import gen.org.tkit.onecx.theme.client.model.*;
import gen.org.tkit.onecx.theme.exim.client.model.*;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;

@QuarkusTest
@LogService
@TestHTTPEndpoint(ThemeRestController.class)
class ThemeRestControllerTest extends AbstractTest {

    KeycloakTestClient keycloakClient = new KeycloakTestClient();
    @InjectMockServerClient
    MockServerClient mockServerClient;

    static final String MOCK_ID = "MOCK";
    static final String WORKSPACE_MOCK_ID = "WORKSPACEMOCK";

    @BeforeEach
    void resetExpectation() {
        try {
            mockServerClient.clear(MOCK_ID);
            mockServerClient.clear(WORKSPACE_MOCK_ID);
        } catch (Exception ex) {
            //  mockId not existing
        }
    }

    @Test
    void getThemeByIdTest() {
        Theme data = new Theme();
        data.setId("test-id-1");
        data.setName("test-name");
        data.setDescription("this is a test theme");

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/" + data.getId()).withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", data.getId())
                .get("/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(GetThemeResponseDTO.class);

        Assertions.assertNotNull(output.getResource());
        Assertions.assertEquals(data.getId(), output.getResource().getId());
        Assertions.assertEquals(data.getName(), output.getResource().getName());
    }

    @Test
    void getThemeByNameTest() {

        Theme data = new Theme();
        data.setName("test-name");

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/name/" + data.getName()).withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("name", data.getName())
                .get("/name/{name}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(GetThemeResponseDTO.class);

        Assertions.assertNotNull(output.getResource());
        Assertions.assertEquals(data.getName(), output.getResource().getName());
    }

    @Test
    void deleteThemeTest() {

        String id = "test-id-1";

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/" + id).withMethod(HttpMethod.DELETE))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));

        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", id)
                .delete("/{id}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    void createThemeTest() {
        CreateTheme data = new CreateTheme();
        data.setName("value1");
        Theme theme = new Theme();
        theme.setName("value1");

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(data)))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.CREATED.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(theme)));

        CreateThemeRequestDTO input = new CreateThemeRequestDTO();
        ThemeUpdateCreateDTO updateCreateDTO = new ThemeUpdateCreateDTO();
        updateCreateDTO.setName("value1");
        input.setResource(updateCreateDTO);

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(input)
                .post()
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(CreateThemeResponseDTO.class);

        // standard USER get FORBIDDEN with only READ permission
        given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(USER))
                .header(APM_HEADER_PARAM, USER)
                .contentType(APPLICATION_JSON)
                .body(input)
                .post()
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

        Assertions.assertNotNull(output.getResource());
        Assertions.assertEquals(data.getName(), output.getResource().getName());
    }

    @Test
    void createThemeFailTest() {
        CreateTheme data = new CreateTheme();
        ProblemDetailResponse problemDetailResponse = new ProblemDetailResponse();
        problemDetailResponse.setErrorCode("CONSTRAINT_VIOLATIONS");
        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(data)))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(problemDetailResponse)));

        CreateThemeRequestDTO input = new CreateThemeRequestDTO();
        ThemeUpdateCreateDTO updateCreateDTO = new ThemeUpdateCreateDTO();
        input.setResource(updateCreateDTO);

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(input)
                .post()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(problemDetailResponse.getErrorCode(), output.getErrorCode());
    }

    @Test
    void getAllThemesTest() {
        Theme t1 = new Theme();
        t1.setId("1");
        t1.setName("test name");

        Theme t2 = new Theme();
        t2.setId("2");
        t2.setName("test name");

        ThemePageResult data = new ThemePageResult();
        data.setNumber(1);
        data.setSize(2);
        data.setTotalElements(2L);
        data.setTotalPages(1L);
        data.setStream(List.of(t1, t2));

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes").withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .get()
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(GetThemesResponseDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getSize(), output.getSize());
        Assertions.assertEquals(data.getStream().size(), output.getStream().size());
        Assertions.assertEquals(data.getStream().get(0).getName(), output.getStream().get(0).getName());
    }

    @Test
    void searchThemeByCriteriaTest() {
        ThemeSearchCriteria criteria = new ThemeSearchCriteria();
        criteria.setPageNumber(1);
        criteria.setName("test");
        criteria.setPageSize(1);

        Theme t1 = new Theme();
        t1.setId("1");
        t1.setName("test");

        ThemePageResult data = new ThemePageResult();
        data.setNumber(1);
        data.setSize(1);
        data.setTotalElements(1L);
        data.setTotalPages(1L);
        data.setStream(List.of(t1));

        SearchThemeRequestDTO searchThemeRequestDTO = new SearchThemeRequestDTO();
        searchThemeRequestDTO.setPageNumber(1);
        searchThemeRequestDTO.setPageSize(1);
        searchThemeRequestDTO.setName("test");

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/search").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(criteria)))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(searchThemeRequestDTO)
                .post("/search")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(SearchThemeResponseDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getSize(), output.getSize());
        Assertions.assertEquals(data.getStream().size(), output.getStream().size());
        Assertions.assertEquals(data.getStream().get(0).getName(), output.getStream().get(0).getName());
    }

    @Test
    void searchThemeByEmptyCriteriaTest() {

        ProblemDetailResponse problemDetailResponse = new ProblemDetailResponse();

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/search").withMethod(HttpMethod.POST))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(problemDetailResponse)));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .post("/search")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ProblemDetailResponse.class);

        Assertions.assertNotNull(output);
    }

    @Test
    void updateThemeTest() {
        String testId = "testId";
        UpdateTheme updateTheme = new UpdateTheme();
        updateTheme.setName("test-name");
        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/" + testId).withMethod(HttpMethod.PUT)
                .withBody(JsonBody.json(updateTheme)))
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));

        Theme theme = new Theme();
        theme.setName("test-name2");
        theme.setId("testId");

        mockServerClient.when(request().withPath("/internal/themes/" + testId).withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withBody(JsonBody.json(theme))
                        .withContentType(MediaType.APPLICATION_JSON));

        ThemeUpdateCreateDTO updateCreateDTO = new ThemeUpdateCreateDTO();
        updateCreateDTO.setName("test-name");
        UpdateThemeRequestDTO input = new UpdateThemeRequestDTO();
        input.setResource(updateCreateDTO);

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", testId)
                .body(input)
                .put("/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(UpdateThemeResponseDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(theme.getName(), output.getResource().getName());
    }

    @Test
    void updateThemeFailTest() {
        String testId = "testId";
        UpdateTheme updateTheme = new UpdateTheme();
        ProblemDetailResponse problemDetailResponse = new ProblemDetailResponse();
        problemDetailResponse.setErrorCode(Response.Status.BAD_REQUEST.toString());
        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/" + testId).withMethod(HttpMethod.PUT)
                .withBody(JsonBody.json(updateTheme)))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .withBody(JsonBody.json(problemDetailResponse))
                        .withContentType(MediaType.APPLICATION_JSON));

        Theme theme = new Theme();
        theme.setName("test-name2");
        theme.setId("testId");

        ThemeUpdateCreateDTO updateCreateDTO = new ThemeUpdateCreateDTO();
        UpdateThemeRequestDTO input = new UpdateThemeRequestDTO();
        input.setResource(updateCreateDTO);

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", testId)
                .body(input)
                .put("/{id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(problemDetailResponse.getErrorCode(), output.getErrorCode());
    }

    @Test
    void getThemeByIdNotFoundTest() {
        String notFoundId = "notFound";
        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/" + notFoundId).withMethod(HttpMethod.GET))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .pathParam("id", notFoundId)
                .get("/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
        Assertions.assertNotNull(output);
    }

    @Test
    void exportImportThemesTest() {
        Set<String> themeNames = new HashSet<>();
        ExportThemeRequest exRequest = new ExportThemeRequest();
        exRequest.setNames(themeNames);
        exRequest.addNamesItem("testTheme");

        EximTheme exportedTheme = new EximTheme();
        exportedTheme.setDescription("exported theme");

        Map<String, EximTheme> themesToExport = new HashMap<>();
        themesToExport.put("testTheme", exportedTheme);

        ThemeSnapshot exResponse = new ThemeSnapshot();
        exResponse.setThemes(themesToExport);

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/exim/v1/themes/export").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(exRequest)))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(exResponse)));

        ExportThemeRequestDTO exRequestDTO = new ExportThemeRequestDTO();
        exRequestDTO.setNames(themeNames);

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(exRequestDTO)
                .post("/export")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ThemeSnapshotDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(output.getThemes().get("testTheme").getDescription(), exportedTheme.getDescription());

        ImportThemeResponse importThemeResponse = new ImportThemeResponse();
        Map<String, ImportThemeResponseStatus> importedThemes = new HashMap<>();
        importedThemes.put("testTheme", ImportThemeResponseStatus.CREATED);
        importThemeResponse.setThemes(importedThemes);

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/exim/v1/themes/import").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(exResponse)))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(importThemeResponse)));

        var importOutput = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(output)
                .post("/import")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ImportThemeResponseDTO.class);

        Assertions.assertNotNull(importOutput);
        Assertions.assertEquals(importOutput.getThemes().get("testTheme").toString(),
                ImportThemeResponseStatus.CREATED.toString());

    }

    @Test
    void serverConstraintTest() {
        ProblemDetailResponse problemDetailResponse = new ProblemDetailResponse();
        problemDetailResponse.setErrorCode("400");
        CreateTheme createTheme = new CreateTheme();
        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(createTheme)))
                .withId(MOCK_ID)
                .respond(httpRequest -> response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(problemDetailResponse)));

        CreateThemeRequestDTO createThemeRequestDTO = new CreateThemeRequestDTO();
        ThemeUpdateCreateDTO themeUpdateCreateDTO = new ThemeUpdateCreateDTO();
        createThemeRequestDTO.setResource(themeUpdateCreateDTO);

        var output = given()
                .when()
                .auth().oauth2(keycloakClient.getAccessToken(ADMIN))
                .header(APM_HEADER_PARAM, ADMIN)
                .contentType(APPLICATION_JSON)
                .body(createThemeRequestDTO)
                .post()
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(output);
    }
}
