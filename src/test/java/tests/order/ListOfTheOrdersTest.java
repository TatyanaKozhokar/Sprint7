package tests.order;

import api.ApiConfig;
import api.OrderApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;

public class ListOfTheOrdersTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = ApiConfig.BASE_URL;
    }

    @Test
    @DisplayName("Getting the List of the orders")
    @Description("Получение списка заказов")
    public void gettingTheListOfTheOrders() {
        Response response = OrderApi.gettingTheListOfTheOrders();
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("orders", notNullValue());

    }
}
