package io.github.onecx.themes.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.theme.bff.clients.model.*;
import gen.io.github.onecx.theme.bff.rs.internal.model.*;
import io.github.onecx.themes.bff.rs.controllers.ThemeRestController;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@LogService
@TestHTTPEndpoint(ThemeRestController.class)
class ThemeRestControllerTest extends AbstractTest {
    @InjectMockServerClient
    MockServerClient mockServerClient;

    @Test
    void getThemeByIdTest() {

        Theme data = new Theme();
        data.setId("test-id-1");
        data.setName("test-name");
        data.setDescription("this is a test theme");

        WorkspaceInfoList workspaces = new WorkspaceInfoList();
        List<WorkspaceInfo> workspaceList = new ArrayList<>();
        WorkspaceInfo workspace = new WorkspaceInfo();
        workspace.setWorkspaceName("workspace1");
        workspace.setDescription("description1");
        workspaceList.add(workspace);
        workspaces.setWorkspaces(workspaceList);

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/" + data.getId()).withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        // create mock rest endpoint for workspace api
        mockServerClient.when(request().withPath("/v1/workspaces/theme/" + data.getName()).withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(workspaces)));

        var output = given()
                .when()
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
        Assertions.assertEquals(workspace.getWorkspaceName(), output.getWorkspaces().get(0).getWorkspaceName());
        Assertions.assertEquals(workspace.getDescription(), output.getWorkspaces().get(0).getDescription());
    }

    @Test
    void getThemeByIdNoWorkspacesTest() {

        Theme data = new Theme();
        data.setId("test-id-1");
        data.setName("test-name");
        data.setDescription("this is a test theme");
        WorkspaceInfoList workspaces = new WorkspaceInfoList();

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/" + data.getId()).withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        // create mock rest endpoint for workspace api
        mockServerClient.when(request().withPath("/v1/workspaces/theme/" + data.getName()).withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withBody(JsonBody.json(workspaces)));

        var output = given()
                .when()
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
        Assertions.assertNull(output.getWorkspaces());
    }

    @Test
    void deleteThemeTest() {

        String id = "test-id-1";

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/" + id).withMethod(HttpMethod.DELETE))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));

        given()
                .when()
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
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.CREATED.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(theme)));

        CreateThemeRequestDTO input = new CreateThemeRequestDTO();
        ThemeUpdateCreateDTO updateCreateDTO = new ThemeUpdateCreateDTO();
        updateCreateDTO.setName("value1");
        input.setResource(updateCreateDTO);

        var output = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(input)
                .post()
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(CreateThemeResponseDTO.class);

        Assertions.assertNotNull(output.getResource());
        Assertions.assertEquals(data.getName(), output.getResource().getName());
    }

    @Test
    void createThemeFailTest() {
        CreateTheme data = new CreateTheme();
        ProblemDetailResponse problemDetailResponse = new ProblemDetailResponse();
        problemDetailResponse.setErrorCode("CONSTRAINT_VIOLATIONS");
        mockServerClient.reset();
        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(data)))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(problemDetailResponse)));

        CreateThemeRequestDTO input = new CreateThemeRequestDTO();
        ThemeUpdateCreateDTO updateCreateDTO = new ThemeUpdateCreateDTO();
        input.setResource(updateCreateDTO);

        var output = given()
                .when()
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
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
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

        ThemeDTO input = new ThemeDTO();
        input.setName("test");
        input.setId("1");

        SearchThemeRequestDTO searchThemeRequestDTO = new SearchThemeRequestDTO();
        searchThemeRequestDTO.setPageNumber(1);
        searchThemeRequestDTO.setPageSize(1);
        searchThemeRequestDTO.setResource(input);

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/search").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(criteria)))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
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
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(problemDetailResponse)));

        var output = given()
                .when()
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
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NO_CONTENT.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON));

        Theme theme = new Theme();
        theme.setName("test-name2");
        theme.setId("testId");

        mockServerClient.when(request().withPath("/internal/themes/" + testId).withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withBody(JsonBody.json(theme))
                        .withContentType(MediaType.APPLICATION_JSON));

        ThemeUpdateCreateDTO updateCreateDTO = new ThemeUpdateCreateDTO();
        updateCreateDTO.setName("test-name");
        UpdateThemeRequestDTO input = new UpdateThemeRequestDTO();
        input.setResource(updateCreateDTO);

        var output = given()
                .when()
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
                .withPriority(100)
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
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.NOT_FOUND.getStatusCode()));

        var output = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", notFoundId)
                .get("/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
        Assertions.assertNotNull(output);
    }

    @Test
    void serverConstraintTest() {
        ProblemDetailResponse problemDetailResponse = new ProblemDetailResponse();
        problemDetailResponse.setErrorCode("400");
        CreateTheme createTheme = new CreateTheme();
        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(createTheme)))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(problemDetailResponse)));

        CreateThemeRequestDTO createThemeRequestDTO = new CreateThemeRequestDTO();

        var output = given()
                .when()
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
