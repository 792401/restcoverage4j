import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class PetOperationsTest extends RestAssuredConfig{


    @Test
    public void getPetById(){
        RestAssured.baseURI = "http://petstore.swagger.io/v2";
        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("/pet/3")
                .then()
                .statusCode(200)
//                .log().all()
                .extract().response();

        System.out.println("Response Body: " + response.asString());
    }

    @Test
    public void getFindByStatus(){
        RestAssured.baseURI = "http://petstore.swagger.io/v2";
        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("/pet/findByStatus?status=available")
                .then()
                .statusCode(200)
//                .log().all()
                .extract().response();

//        System.out.println("Response Body: " + response.asString());
    }
}