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
                .post(Endpoints.courier);
    }

    public static Response login(CourierData courierData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courierData)
                .when()
                .post(Endpoints.login);
    }

    public static Response deleteCourier(int courierId) {
        return given()
                .pathParam("id", courierId)
                .when()
                .delete(Endpoints.deleteCourier);
    }

    public static Response requestWithoutRequiredField(CourierData courierWithTheWrongData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courierWithTheWrongData)
                .when()
                .post(Endpoints.login);
    }

    public static Response createCourierWithTheSameLogin(CourierData courierWithTheWrongData) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courierWithTheWrongData)
                .when()
                .post(Endpoints.courier);
    }
}


