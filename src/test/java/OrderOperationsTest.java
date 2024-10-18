import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class OrderOperationsTest extends RestAssuredConfig{

    @Test
    public void createOrder() {
        String requestBody = "{\n" +
                "  \"id\": 3,\n" +
                "  \"petId\": 2,\n" +
                "  \"quantity\": 2,\n" +
                "  \"shipDate\": \"2024-10-18T14:24:28.900Z\",\n" +
                "  \"status\": \"placed\",\n" +
                "  \"complete\": true\n" +
                "}";
        Response response = given()
                .baseUri("http://petstore.swagger.io/v2")
                .header("Content-Type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/store/order")
                .then()
//                .statusCode(200)
                .log().all()
                .extract().response();

        System.out.println("Response Body: " + response.asString());
    }
}
