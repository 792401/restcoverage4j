import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class PetOperationsTest extends RestAssuredConfig {



    @Test
    public void getPetById() {
        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("/pet/1")
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println("Response Body: " + response.asString());
    }

    @Test
    public void getFindByStatus() {
        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("/pet/findByStatus?status=available")
                .then()
                .statusCode(200)
                .extract().response();

//        System.out.println("Response Body: " + response.asString());
    }
}
