import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class StoreOperationsTest extends RestAssuredConfig{

    @Test
    public void getStoreInventory(){
        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
//
                .extract().response();

        System.out.println("Response Body: " + response.asString());
    }
}
