package CreateCourier;

import CourierData.CourierData;
import CourierData.CourierDuplicateResponse;
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

public class CreateACourierTest {
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

    @Step("attempt to create courier with empty required field")
    public void attemptToCreateCourierWithEmptyRequiredField(){
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierData)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("attempt to create courier with the same data")
    public void attemptToCreateCourierWithTheSameData(){
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierData)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .extract()
                .as(CourierDuplicateResponse.class);
    }

    @Step("attempt to create courier with the same data")
    public void attemptToCreateCourierWithTheSameLogin(){
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierWithTheWrongData)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .extract()
                .as(CourierDuplicateResponse.class);
    }

    @Test
    @DisplayName("Checking the creation of a courier with all the data")
    public void createNewCourierWithFullDataTest(){
        courierData = new CourierData("MrKirkorov1111", "112233", "Philipp");
        createCourier();
        loginAndGetCourierId();
    }

    @Test
    @DisplayName("Checking the impossibility of creating a courier with an empty Password field")
    public void createNewCourierWithoutRequiredFieldPasswordTest(){
        courierData = new CourierData("MrKirkorov112", null, "Philipp");
        attemptToCreateCourierWithEmptyRequiredField();
    }

    @Test
    @DisplayName("Checking the impossibility of creating of a courier with an empty Login field")
    public void createNewCourierWithoutRequiredFieldLoginTest(){
        courierData = new CourierData(null, "1234", "Philipp");
        attemptToCreateCourierWithEmptyRequiredField();
    }

    @Test
    @DisplayName("Checking the impossibility of creating a duplicate courier")
    public void impossibilityToCreateDoubleCourierTest(){
        courierData = new CourierData("MrKirkorov113", "112233", "Philipp");
        createCourier();
        attemptToCreateCourierWithTheSameData();
        loginAndGetCourierId();

    }

    @Test
    @DisplayName("Checking the impossibility of creating a courier with the same Login")
    public void impossibilityToCreateCourierWithTheSameLoginTest(){
        courierData = new CourierData("MrKirkorov114", "112233", "Philipp");
        courierWithTheWrongData = new CourierData("MrKirkorov111", "00000", "Oleg");
        createCourier();
        attemptToCreateCourierWithTheSameData();
        loginAndGetCourierId();

    }

    @Test
    @DisplayName("Checking the creation of a courier with an empty Firstname field")
    public void createNewCourierWithoutFieldFirstnameTest(){
        courierData = new CourierData("MrKirkorov115", "112233", null);
        createCourier();
        loginAndGetCourierId();
    }

    @After
    public void deleteData() {

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
