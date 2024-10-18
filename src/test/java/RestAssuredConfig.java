import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.service.restcoverage4j.restassured.LoggingFilter;

public class RestAssuredConfig {

    @BeforeAll
    public static void setupRestAssured() {
        LoggingFilter loggingFilter = new LoggingFilter();

        RestAssured.filters(loggingFilter);

        RestAssured.baseURI = "http://petstore.swagger.io/v2";
    }

    
}