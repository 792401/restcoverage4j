import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class petOperations extends RestAssuredConfig{


    @Test
    public void getPetById(){
        RestAssured.baseURI = "http://petstore.swagger.io/v2";
        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("/pet/3")
                .then()
                .statusCode(200)
                .log().all() // This will log the response to the console
                .extract().response();

        // Print the response body
        System.out.println("Response Body: " + response.asString());


    }
}
