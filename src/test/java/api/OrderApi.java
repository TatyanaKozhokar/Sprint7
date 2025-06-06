package api;

import data.OrderData;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderApi {
    public static Response createAnOrder(OrderData orderData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(orderData)
                .when()
                .post(Endpoints.orders);
    }

    public static Response deleteAnOrder(int trackNumber) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(trackNumber)
                .when()
                .put(Endpoints.deleteCourier);
    }

    public static Response gettingTheListOfTheOrders() {
        return given()
                .when()
                .get(Endpoints.orders);
    }
}
