package api;

import data.CourierData;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierApi {
    public static Response createCourier(CourierData courierData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courierData)
                .when()
                .post(Endpoints.COURIER);
    }

    public static Response login(CourierData courierData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courierData)
                .when()
                .post(Endpoints.LOGIN);
    }

    public static Response deleteCourier(int courierId) {
        return given()
                .pathParam("id", courierId)
                .when()
                .delete(Endpoints.DELETE_COURIER);
    }

    public static Response requestWithoutRequiredField(CourierData courierWithTheWrongData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courierWithTheWrongData)
                .when()
                .post(Endpoints.LOGIN);
    }

    public static Response createCourierWithTheSameLogin(CourierData courierWithTheWrongData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courierWithTheWrongData)
                .when()
                .post(Endpoints.COURIER);
    }
}


