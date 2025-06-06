package tests.order;

import api.OrderApi;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;

public class ListOfTheOrdersTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Getting the List of the orders")
    public void gettingTheListOfTheOrders() {
        Response response = OrderApi.gettingTheListOfTheOrders();
        response.then()
                .statusCode(200)
                .body("orders", notNullValue());

    }
}
