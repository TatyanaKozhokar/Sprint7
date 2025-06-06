package tests.courier.login;

import api.CourierApi;
import data.CourierData;
import data.CourierLoginResponse;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CourierLoginTest {
    Faker faker = new Faker();
    private int courierId;
    CourierData courierData;
    CourierData courierWithTheWrongData;

    @Before
    public void setUp() {
        courierData = new CourierData(faker.name().username(), faker.internet().password(6, 12), faker.name().firstName());
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        createCourier();
    }

    @Step("Create new courier")
    public void createCourier() {
        Response response = CourierApi.createCourier(courierData);
        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Login")
    public CourierLoginResponse login() {
        Response response = CourierApi.login(courierData);
                return response.then()
                        .statusCode(200)
                        .extract()
                        .as(CourierLoginResponse.class);

    }

    @Step("Request Without required field")
    public void requestWithoutRequiredField() {
        Response response = CourierApi.requestWithoutRequiredField(courierWithTheWrongData);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step("Request With the wrong data")
    public void requestWithTheWrongData() {
        Response response = CourierApi.requestWithoutRequiredField(courierWithTheWrongData);
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Checking log in with the valid data")
    public void loginWithTheValidDataTest() {
        courierId = login().getId();
        assertThat(courierId, notNullValue());
    }

    @Test
    @DisplayName("Attempt to log in when the field Login is empty")
    public void attemptToLogInWhenTheFieldLoginIsEmpty() {
        courierWithTheWrongData = new CourierData(null, "112233", "Nikolay");
        requestWithoutRequiredField();
    }

    @Test
    @DisplayName("Attempt to log in when the field Password is empty")
    public void attemptToLogInWhenTheFieldPasswordIsEmpty() {
        courierWithTheWrongData = new CourierData("Baskov", null, "Nikolay");
        requestWithoutRequiredField();
    }

    @Test
    @DisplayName("Attempt to log in when the field Login is wrong")
    public void attemptToLogInWhenTheFieldLoginIsWrong() {
        courierWithTheWrongData = new CourierData("askov", "112233", "Nikolay");
        requestWithTheWrongData();
    }

    @Test
    @DisplayName("Attempt to log in when the field Password is wrong")
    public void attemptToLogInWhenTheFieldPasswordIsWrong() {
        courierWithTheWrongData = new CourierData("Baskov", "112234", "Nikolay");
        requestWithTheWrongData();
    }

    @After
    public void deleteData() {
        courierId = login().getId();
        if (courierId != 0) {
            Response response = CourierApi.deleteCourier(courierId);
            response.then()
                    .statusCode(200)
                    .body("ok", equalTo(true));
        }
    }
}
