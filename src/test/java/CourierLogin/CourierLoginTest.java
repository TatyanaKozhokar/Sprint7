package CourierLogin;

import CourierData.CourierData;
import CourierData.CourierLoginResponse;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CourierLoginTest {
    private int courierId;
    CourierData courierData;
    CourierData courierWithTheWrongData;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Step("Create new courier")
    public void createCourier() {
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierData)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Login and get courier ID")
    public void loginAndGetCourierId() {
        CourierLoginResponse loginResponse =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courierData)
                        .when()
                        .post("/api/v1/courier/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(CourierLoginResponse.class);

        courierId = loginResponse.getId();
        assertThat(courierId, notNullValue());
    }

    @Step("Request Without required field")
    public void requestWithoutRequiredField(){
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierWithTheWrongData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step("Request With the wrong data")
    public void requestWithTheWrongData(){
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierWithTheWrongData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Checking log in with the valid data")
    public void loginWithTheValidDataTest(){
        courierData = new CourierData("Baskov13", "112233", "Nikolay");
        createCourier();
        loginAndGetCourierId();
    }
    @Test
    @DisplayName("Attempt to log in when the field Login is empty")
    public void attemptToLogInWhenTheFieldLoginIsEmpty(){
        courierData = new CourierData("Baskov2", "112233", "Nikolay");
        courierWithTheWrongData = new CourierData(null, "112233", "Nikolay");
        createCourier();
        requestWithoutRequiredField();
    }
    @Test
    @DisplayName("Attempt to log in when the field Password is empty")
    public void attemptToLogInWhenTheFieldPasswordIsEmpty(){
        courierData = new CourierData("Baskov31", "112233", "Nikolay");
        courierWithTheWrongData = new CourierData("Baskov", null, "Nikolay");
        createCourier();
        requestWithoutRequiredField();
    }

    @Test
    @DisplayName("Attempt to log in when the field Login is wrong")
    public void attemptToLogInWhenTheFieldLoginIsWrong(){
        courierData = new CourierData("Baskov41", "112233", "Nikolay");
        courierWithTheWrongData = new CourierData("askov", "112233", "Nikolay");
        createCourier();
        requestWithTheWrongData();
    }

    @Test
    @DisplayName("Attempt to log in when the field Password is wrong")
    public void attemptToLogInWhenTheFieldPasswordIsWrong(){
        courierData = new CourierData("Baskov51", "112233", "Nikolay");
        courierWithTheWrongData = new CourierData("Baskov", "112234", "Nikolay");
        createCourier();
        requestWithTheWrongData();
    }

    @After
    public void deleteData() {
        loginAndGetCourierId();
        if (courierId != 0) {
            given()
                    .pathParam("id", courierId)
                    .when()
                    .delete("/api/v1/courier/{id}")
                    .then()
                    .statusCode(200)
                    .body("ok", equalTo(true));
        }
    }
}
