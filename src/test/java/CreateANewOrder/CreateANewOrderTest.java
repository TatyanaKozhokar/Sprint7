package CreateANewOrder;


import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class CreateANewOrderTest {
    private int trackNumber;
    private final OrderData orderData;

    public CreateANewOrderTest(String firstName, String lastName, String address,
                     int metroStation, String phone, int rentTime,
                     String deliveryDate, String comment, List<String> color) {
        this.orderData = new OrderData(
                firstName, lastName, address, metroStation,
                phone, rentTime, deliveryDate, comment, color
        );
    }

    @Parameterized.Parameters(name = "Тестовые данные: {0} {1}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][] {
                {
                        "Oleg",
                        "Gazmanow",
                        "Kremlin",
                        4,
                        "+7 800 555 35 35",
                        5,
                        "2020-06-06",
                        "Ya yasnie dni ostavil sebe",
                        List.of("BLACK")
                },

                {
                        "Oleg",
                        "Gazmanow",
                        "Kremlin",
                        4,
                        "+7 800 555 35 35",
                        5,
                        "2020-06-06",
                        "Ya yasnie dni ostavil sebe",
                        List.of("BLACK", "GRAY")
                },
                {
                        "Oleg",
                        "Gazmanow",
                        "Kremlin",
                        "+7 800 555 35 35",
                        5,
                        "2020-06-06",
                        "Ya yasnie dni ostavil sebe",
                        null
                }
        });
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        RestAssured.filters(new AllureRestAssured());

    }

    @Step("Creating an Order")
    public void createAnOrder(){
        OrderTrack orderTrack =
            given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(orderData)
                    .when()
                    .post("/api/v1/orders")
                    .then()
                    .statusCode(201)
                    .extract()
                    .as(OrderTrack.class);
        trackNumber = orderTrack.getNumber();
        assertThat(trackNumber, notNullValue());
    }

    @Test
    @DisplayName("Create an order")
    public void creatingAnOrder(){
        createAnOrder();
    }

    @After
    public void deleteAnOrder(){
        if (trackNumber!=0){
            given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(trackNumber)
                    .when()
                    .put("/api/v1/orders/cancel")
                    .then()
                    .statusCode(200)
                    .body("ok", equalTo(true));

        }

    }

}

