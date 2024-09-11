import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class RestAssuredConfig {

    @BeforeAll
    public static void setupRestAssured() {
        // Set up Rest Assured with the custom LoggingFilter globally
        RestAssured.filters(new LoggingFilter());

        // Optionally, set base URI globally
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }
}