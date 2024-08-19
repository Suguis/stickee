package cat.siesta.stickee.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import cat.siesta.stickee.config.StickeeConfig;
import cat.siesta.stickee.persistence.Note;
import cat.siesta.stickee.service.NoteService;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import jakarta.annotation.PostConstruct;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class NoteControllerIntegrationTest {

    private Note noteHello = new Note("Hello world!");
    private Note noteBye = new Note("Bye!");
    private Note noteHtml = new Note("<b>Bold</b>");
    private Note noteJson = new Note("{ \"text\": \"Hello\" }");

    @Autowired
    StickeeConfig stickeeConfig;

    @Autowired
    NoteService noteService;

    @LocalServerPort
    Integer port;

    @PostConstruct
    void setUp() {
        RestAssured.port = port;
        RestAssured.registerParser("text/plain", Parser.TEXT);
    }

    @Test
    void shouldGetWhenExisting() {
        String noteHelloId = noteService.create(noteHello).getId().orElseThrow();
        String noteByeId = noteService.create(noteBye).getId().orElseThrow();

        given().get(stickeeConfig.getNotesBasePath() + "/" + noteHelloId).then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("Hello world!"))
                .contentType("text/plain");

        given().get(stickeeConfig.getNotesBasePath() + "/" + noteByeId).then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("Bye!"))
                .contentType("text/plain");
    }

    @Test
    void shouldAlwaysReturnPlainText() {
        String noteHtmlId = noteService.create(noteHtml).getId().orElseThrow();
        String noteJsonId = noteService.create(noteJson).getId().orElseThrow();

        given().header("Accept", "text/html")
                .given().get(stickeeConfig.getNotesBasePath() + "/" + noteHtmlId).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType("text/plain");

        given().header("Accept", "application/json")
                .given().get(stickeeConfig.getNotesBasePath() + "/" + noteJsonId).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType("text/plain");
    }

    @Test
    void shouldGetNotFoundWhenNotExisting() {
        given().get(stickeeConfig.getNotesBasePath() + "/" + UUID.randomUUID()).then().assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldCreateAndGet() {
        var text = "Posting!";

        var response = given()
                .param("text", text)
                .post(stickeeConfig.getNotesBasePath() + "/create").then().assertThat()
                .statusCode(HttpStatus.FOUND.value())
                .extract();

        var location = response.header("Location");

        given().get(location).then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo(text));
    }

    @Test
    void shouldContainCacheHeadersOnGet() {
        var marginSeconds = 5;

        var noteId = noteService.create(noteHello).getId().orElseThrow();
        var expectedCache = stickeeConfig.getNoteMaxAge();
        Stream<String> validRange = IntStream.range(-marginSeconds, marginSeconds)
                .mapToObj(margin -> "max-age=" + (expectedCache + margin) + ", public, immutable");

        var cacheControlValue = given().get(stickeeConfig.getNotesBasePath() + "/" + noteId)
                .header("Cache-control");

        assertTrue(validRange.anyMatch(valid -> valid.equals(cacheControlValue)));
    }
}
