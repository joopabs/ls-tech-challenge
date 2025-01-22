package tech.challenge.speech.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class SpeechControllerIntegrationTest {

    @LocalServerPort
    private int port;

    // Define the PostgreSQL container
    @Container
    static PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("app_db")
                .withUsername("app_user")
                .withPassword("Beyond80.Bucks");
    }

    @BeforeAll
    static void startContainer() {
        // Optionally, ensure the container is started
        postgresContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void setup() {
        // Set RestAssured port
        RestAssured.port = port;
    }

    @Test
    void shouldSearchSpeechesByAuthor() {
        given()
                .queryParam("author", "John Doe")
                .when()
                .get("/api/speeches/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$.size()", is(2)) // 2 speeches from seed data authored by John Doe
                .body("content", hasItems("Equality and justice for all", "Economic stability is key to peace"));
    }

    @Test
    void shouldSearchSpeechesBySnippet() {
        given()
                .queryParam("snippet", "rights")
                .when()
                .get("/api/speeches/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$.size()", is(1)) // Speech 2 from seed data contains "rights"
                .body("author[0]", is("Jane Smith"));
    }

    @Test
    void shouldSearchSpeechesByDateRange() {
        given()
                .queryParam("startDate", "2023-01-01T00:00:00Z")
                .queryParam("endDate", "2023-02-28T23:59:59Z")
                .when()
                .get("/api/speeches/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$.size()", is(2)) // Speech1 and Speech2 from seed data are within this date range
                .body("author", hasItems("John Doe", "Jane Smith"));
    }

    @Test
    void shouldSearchSpeechesByKeywords() {
        given()
                .queryParam("keywords", "economy,freedom") // Send keywords as a comma-separated list
                .when()
                .get("/api/speeches/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$.size()", is(2)) // Assert that 2 results were returned
                .body("content", hasItems("Human rights are non-negotiable", "Economic stability is key to peace"));
    }


    @Test
    void shouldSearchSpeechesByAuthorAndKeywords() {
        given()
                .queryParam("author", "John Doe")
                .queryParam("keywords", "economy")
                .when()
                .get("/api/speeches/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$.size()", is(1)) // Only one speech matches both criteria
                .body("content[0]", is("Economic stability is key to peace"))
                .body("author[0]", is("John Doe"))
                .body("keywords[0]", hasItems("economy", "peace"));
    }

    @Test
    void shouldSearchSpeechesByAllParameters() {
        given()
                .queryParam("author", "John Doe")
                .queryParam("snippet", "justice")
                .queryParam("startDate", "2023-01-01T00:00:00Z")
                .queryParam("endDate", "2023-01-02T23:59:59Z")
                .queryParam("keywords", "equality,justice")
                .when()
                .get("/api/speeches/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$.size()", is(1)) // Only one speech matches both criteria
                .body("author[0]", is("John Doe"))
                .body("content[0]", is("Equality and justice for all"))
                .body("speechDate[0]", is("2023-01-01T10:00:00Z"))
                .body("keywords[0]", hasItems("equality", "justice"));
    }

    @Test
    void shouldReturnEmptyWhenNoResultsFound() {
        given()
                .queryParam("author", "Nonexistent Author")
                .when()
                .get("/api/speeches/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$.size()", is(0)); // Assert that no results were found
    }
}