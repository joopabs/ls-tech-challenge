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

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

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
                .queryParam("author", "John Doe").log().all()
                .when()
                .get("/api/speeches/search")
                .then().log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", is(200))
                .body("message", is("Speeches retrieved successfully"))
                .body("data.size()", is(2)) // 2 speeches from seed data authored by John Doe
                .body("data.content", hasItems("Equality and justice for all", "Economic stability is key to peace"));
    }

    @Test
    void shouldSearchSpeechesBySnippet() {
        given()
                .queryParam("snippet", "rights").log().all()
                .when()
                .get("/api/speeches/search")
                .then().log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", is(200))
                .body("message", is("Speeches retrieved successfully"))
                .body("data.size()", is(1)) // Speech 2 from seed data contains "rights"
                .body("data[0].author", is("Jane Smith"));
    }

    @Test
    void shouldSearchSpeechesByDateRangeUTC() {
        given()
                .queryParam("startDate", "2023-01-01T10:00:00Z")
                .queryParam("endDate", "2023-02-15T15:30:00Z").log().all()
                .when()
                .get("/api/speeches/search")
                .then().log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", is(200))
                .body("message", is("Speeches retrieved successfully"))
                .body("data.size()", is(2)) // Speech1 and Speech2 from seed data are within this date range
                .body("data.author", hasItems("John Doe", "Jane Smith"));
    }

    @Test
    void shouldSearchSpeechesByDateRangeUTCPlus8() {
        given()
                .queryParam("startDate", "2023-01-01T18:00:00+08:00")
                .queryParam("endDate", "2023-02-15T23:30:00+08:00").log().all()
                .when()
                .get("/api/speeches/search")
                .then().log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", is(200))
                .body("message", is("Speeches retrieved successfully"))
                .body("data.size()", is(2)) // Speech1 and Speech2 from seed data are within this date range
                .body("data.author", hasItems("John Doe", "Jane Smith"));
    }

    @Test
    void shouldSearchSpeechesByKeywords() {
        given()
                .queryParam("keywords", "RIGHTS,Peace").log().all() // Send keywords as a comma-separated list
                .when()
                .get("/api/speeches/search")
                .then().log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", is(200))
                .body("message", is("Speeches retrieved successfully"))
                .body("data.size()", is(2)) // Assert that 2 results were returned
                .body("data.content", hasItems("Human rights are non-negotiable", "Economic stability is key to peace"));
    }

    @Test
    void shouldSearchSpeechesByAuthorAndKeywords() {
        given()
                .queryParam("author", "John Doe")
                .queryParam("keywords", "economy").log().all()
                .when()
                .get("/api/speeches/search")
                .then().log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", is(200))
                .body("message", is("Speeches retrieved successfully"))
                .body("data.size()", is(1)) // Assert that 2 results were returned
                .body("data[0].content", is("Economic stability is key to peace"))
                .body("data[0].author", is("John Doe"))
                .body("data[0].keywords", hasItems("economy", "peace"));
    }

    @Test
    void shouldSearchSpeechesByAllParameters() {
        given()
                .queryParam("author", "John Doe")
                .queryParam("snippet", "justice")
                .queryParam("startDate", "2023-01-01T00:00:00Z")
                .queryParam("endDate", "2023-01-02T23:59:59Z")
                .queryParam("keywords", "equality,justice").log().all()
                .when()
                .get("/api/speeches/search")
                .then().log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", is(200))
                .body("message", is("Speeches retrieved successfully"))
                .body("data.size()", is(1)) // Only one speech matches both criteria
                .body("data[0].author", is("John Doe"))
                .body("data[0].content", is("Equality and justice for all"))
                .body("data[0].speechDate", is("2023-01-01T10:00:00Z"))
                .body("data[0].keywords", hasItems("equality", "justice"));
    }

    @Test
    void shouldReturnEmptyWhenNoResultsFound() {
        given()
                .queryParam("author", "Nonexistent Author").log().all()
                .when()
                .get("/api/speeches/search")
                .then().log().all()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("status", is(404)) // Verify that status matches HTTP 201
                .body("message", is("No speeches found matching the search criteria"));
    }

    @Test
    void shouldCreateSpeechSuccessfully() {
        String requestBody = """
                    {
                        "content": "The journey towards equality begins today.",
                        "author": "Amanda Gorman",
                        "keywords": ["equality", "justice", "freedom"],
                        "speechDate": "2023-11-05T00:00:00+08:00"
                    }
                """;

        final String now = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/speeches")
                .then().log().all()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("status", is(201)) // Verify that status matches HTTP 201
                .body("message", is("Speech created successfully"))
                .body("data.id", is(notNullValue()))
                .body("data.content", is("The journey towards equality begins today."))
                .body("data.author", is("Amanda Gorman"))
                .body("data.keywords", hasItems("equality", "justice", "freedom"))
                .body("data.speechDate", is("2023-11-04T16:00:00Z"))
                .body("data.createDateTime", is(now))
                .body("data.updateDateTime", is(now));
    }

    @Test
    void shouldReturnBadRequestForMissingRequiredFields() {
        String invalidRequestBody = """
                    {
                        "author": "Amanda Gorman"
                    }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequestBody)
                .when()
                .post("/api/speeches")
                .then().log().all()
                .statusCode(400) // HTTP 400 Bad Request
                .contentType(ContentType.JSON)
                .body("status", is(400))
                .body("message", is("Validation failed"))
                .body("data", is(nullValue()))
                .body("errors", notNullValue())
                .body("errors", hasItem(containsString("content")));
    }
}