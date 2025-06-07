package tests.courier.login;

import api.ApiConfig;
import api.CourierApi;
import data.CourierData;
import data.CourierLoginResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.apache.http.HttpStatus;
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
        RestAssured.baseURI = ApiConfig.BASE_URL;
        Response response = CourierApi.createCourier(courierData);
        response.then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Step("Login")
    public CourierLoginResponse login() {
        Response response = CourierApi.login(courierData);
                return response.then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .as(CourierLoginResponse.class);

    }

    @Step("Request Without required field")
    public void requestWithoutRequiredField() {
        Response response = CourierApi.requestWithoutRequiredField(courierWithTheWrongData);
        response.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step("Request With the wrong data")
    public void requestWithTheWrongData() {
        Response response = CourierApi.requestWithoutRequiredField(courierWithTheWrongData);
        response.then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Checking log in with the valid data")
    @Description("Проверка возможности залогиниться с валидными данными")
    public void loginWithTheValidDataTest() {
        courierId = login().getId();
        assertThat(courierId, notNullValue());
    }

    @Test
    @DisplayName("Attempt to log in when the field Login is empty")
    @Description("Проверка возможности залогиниться с пустым полем Логин")
    public void attemptToLogInWhenTheFieldLoginIsEmpty() {
        courierWithTheWrongData = new CourierData(null, courierData.getPassword(), courierData.getFirstname());
        requestWithoutRequiredField();
    }

    @Test
    @DisplayName("Attempt to log in when the field Password is empty")
    @Description("Проверка возможности залогиниться с пустым полем пароль")
    public void attemptToLogInWhenTheFieldPasswordIsEmpty() {
        courierWithTheWrongData = new CourierData(courierData.getLogin(), null, courierData.getFirstname());
        requestWithoutRequiredField();
    }

    @Test
    @DisplayName("Attempt to log in when the field Login is wrong")
    @Description("Проверка возможности залогиниться с неправильным логином")
    public void attemptToLogInWhenTheFieldLoginIsWrong() {
        courierWithTheWrongData = new CourierData(faker.name().username(), courierData.getPassword(), courierData.getFirstname());
        requestWithTheWrongData();
    }

    @Test
    @DisplayName("Attempt to log in when the field Password is wrong")
    @Description("Проверка возможности залогиниться с неправильным паролем")
    public void attemptToLogInWhenTheFieldPasswordIsWrong() {
        courierWithTheWrongData = new CourierData(courierData.getLogin(), faker.internet().password(6,12), courierData.getFirstname());
        requestWithTheWrongData();
    }

    @After
    public void deleteData() {
        courierId = login().getId();
        if (courierId != 0) {
            Response response = CourierApi.deleteCourier(courierId);
            response.then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("ok", equalTo(true));
        }
    }
}
