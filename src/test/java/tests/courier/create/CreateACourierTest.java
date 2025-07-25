package tests.courier.create;

import api.ApiConfig;
import api.CourierApi;
import data.CourierData;
import data.CourierDuplicateResponse;
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

public class CreateACourierTest {
    Faker faker = new Faker();
    CourierData courierData;
    CourierData courierWithTheWrongData;

    @Before
    public void setUp() {
        RestAssured.baseURI = ApiConfig.BASE_URL;
    }

    @Step("Create new courier")
    public void createCourier() {
        Response response = CourierApi.createCourier(courierData);
        response.then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Step("Login and get courier ID")
    public CourierLoginResponse login() {
        Response response = CourierApi.login(courierData);
        return response.then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(CourierLoginResponse.class);
    }

    @Step("attempt to create courier with empty required field")
    public void attemptToCreateCourierWithEmptyRequiredField() {
        Response response = CourierApi.createCourier(courierData);
        response.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("attempt to create courier with the same data")
    public void attemptToCreateCourierWithTheSameData() {
        Response response = CourierApi.createCourier(courierData);
        response.then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .extract()
                .as(CourierDuplicateResponse.class);
    }

    @Step("attempt to create courier with the same login")
    public void attemptToCreateCourierWithTheSameLogin() {
        Response response = CourierApi.createCourierWithTheSameLogin(courierWithTheWrongData);
        response.then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется"))
                .extract()
                .as(CourierDuplicateResponse.class);
    }

    @Test
    @DisplayName("Checking the creation of a courier with all the data")
    @Description("Проверка возможности создания курьера с валидными данными")
    public void createNewCourierWithFullDataTest() {
        courierData = new CourierData(faker.name().username(), faker.internet().password(6, 12), faker.name().firstName());
        createCourier();
    }

    @Test
    @DisplayName("Checking the impossibility of creating a courier with an empty Password field")
    @Description("Проверка возможности создания курьера с пустым полем Пароль")
    public void createNewCourierWithoutRequiredFieldPasswordTest() {
        courierData = new CourierData(faker.name().username(), faker.internet().password(6, 12), faker.name().firstName());
        attemptToCreateCourierWithEmptyRequiredField();
    }

    @Test
    @DisplayName("Checking the impossibility of creating of a courier with an empty Login field")
    @Description("Проверка возможности создания курьера с пустым полем Логин")
    public void createNewCourierWithoutRequiredFieldLoginTest() {
        courierData = new CourierData(null, faker.internet().password(6, 12), faker.name().firstName());
        attemptToCreateCourierWithEmptyRequiredField();
    }

    @Test
    @DisplayName("Checking the impossibility of creating a duplicate courier")
    @Description("Проверка возможности создания двух одинаковых курьеров")
    public void impossibilityToCreateDoubleCourierTest() {
        courierData = new CourierData(faker.name().username(), faker.internet().password(6, 12), faker.name().firstName());
        createCourier();
        attemptToCreateCourierWithTheSameData();
    }

    @Test
    @DisplayName("Checking the impossibility of creating a courier with the same Login")
    @Description("Проверка возможности создания курьера с существующим логином")
    public void impossibilityToCreateCourierWithTheSameLoginTest() {
        String sameLogin = faker.name().username();
        courierData = new CourierData(sameLogin, faker.internet().password(6, 12), faker.name().firstName());
        courierWithTheWrongData = new CourierData(sameLogin, faker.internet().password(6, 12), faker.name().firstName());
        createCourier();
        attemptToCreateCourierWithTheSameLogin();

    }

    @Test
    @DisplayName("Checking the creation of a courier with an empty Firstname field")
    @Description("Проверка возможности создания курьера с пустым полем Firstname")
    public void createNewCourierWithoutFieldFirstnameTest() {
        courierData = new CourierData(faker.name().username(), faker.internet().password(6, 12),  null);
        createCourier();
    }

    @After
    public void deleteData() {
        if (courierData.getLogin() != null) {  // Только если логин был указан
            int courierId = login().getId();
            if (courierId != 0) {
                CourierApi.deleteCourier(courierId);
            }
        }
    }
}
