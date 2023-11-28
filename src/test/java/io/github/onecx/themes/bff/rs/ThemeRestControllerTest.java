package io.github.onecx.themes.bff.rs;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.List;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;

import gen.io.github.onecx.theme.bff.clients.model.Theme;
import gen.io.github.onecx.theme.bff.clients.model.ThemePageResult;
import gen.io.github.onecx.theme.bff.clients.model.ThemeSearchCriteria;
import gen.io.github.onecx.theme.bff.clients.model.UpdateTheme;
import gen.io.github.onecx.theme.bff.rs.internal.model.*;
import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ThemeRestControllerTest extends AbstractTest {
    @InjectMockServerClient
    MockServerClient mockServerClient;

    @Test
    void getThemeByIdTest() {

        Theme data = new Theme();
        data.setId("test-id-1");
        data.setName("test-name");
        data.setDescription("this is a test theme");

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/" + data.getId()).withMethod(HttpMethod.GET))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));

        var output = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", data.getId())
                .get("/themes/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(GetThemeResponseDTO.class);

        Assertions.assertNotNull(output.getResource());
        Assertions.assertEquals(data.getId(), output.getResource().getId());
        Assertions.assertEquals(data.getName(), output.getResource().getName());
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
                .delete("/themes/{id}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode())
                .contentType(APPLICATION_JSON);
    }

    @Test
    void createThemeTest() {
        ThemeDTO data = new ThemeDTO();
        data.setId("app1");
        data.setName("value1");
        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes").withMethod(HttpMethod.POST)
                .withBody(JsonBody.json(data)))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.CREATED.getStatusCode())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(JsonBody.json(data)));
        CreateThemeRequestDTO input = new CreateThemeRequestDTO();
        input.setResource(data);

        var output = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(input)
                .post("/themes")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(CreateThemeResponseDTO.class);

        Assertions.assertNotNull(output.getResource());
        Assertions.assertEquals(data.getId(), output.getResource().getId());
        Assertions.assertEquals(data.getName(), output.getResource().getName());
    }

    @Test
    void getAllApplicationParametersTest() {
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
                .get("/themes")
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
                .post("/themes/search")
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
    void updateParameterValueTest() {

        String id = "test-update-1";

        UpdateTheme data = new UpdateTheme();
        data.description("description1");
        data.name("test1");

        // create mock rest endpoint
        mockServerClient.when(request().withPath("/internal/themes/" + id).withMethod(HttpMethod.PUT)
                .withBody(JsonBody.json(data)))
                .withPriority(100)
                .respond(httpRequest -> response().withStatusCode(Response.Status.OK.getStatusCode())
                        .withBody(JsonBody.json(data))
                        .withContentType(MediaType.APPLICATION_JSON));

        UpdateThemeRequestDTO input = new UpdateThemeRequestDTO();
        ThemeDTO dto = new ThemeDTO();
        dto.setId("1");
        dto.setName("test1");
        dto.setDescription("description1");
        input.setResource(dto);

        var output = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", id)
                .body(input)
                .put("/themes/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(UpdateThemeResponseDTO.class);

        Assertions.assertNotNull(output);
        Assertions.assertEquals(data.getName(), output.getResource().getName());
        Assertions.assertEquals(data.getDescription(), output.getResource().getDescription());
    }
}
