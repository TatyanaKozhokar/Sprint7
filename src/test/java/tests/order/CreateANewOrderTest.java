package tests.order;


import api.OrderApi;
import data.OrderData;
import data.OrderTrack;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class CreateANewOrderTest {
    static Faker faker = new Faker();
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return Arrays.asList(new Object[][]{
                {
                        faker.name().firstName(),
                        faker.name().lastName(),
                        faker.address().streetAddress(),
                        faker.number().numberBetween(1, 50),
                        faker.phoneNumber().phoneNumber(),
                        faker.number().numberBetween(1, 10),
                        sdf.format(faker.date().future(30, TimeUnit.DAYS)),
                        faker.lorem().sentence(),
                        List.of("BLACK")
                },
                {
                        faker.name().firstName(),
                        faker.name().lastName(),
                        faker.address().streetAddress(),
                        faker.number().numberBetween(1, 50),
                        faker.phoneNumber().phoneNumber(),
                        faker.number().numberBetween(1, 10),
                        sdf.format(faker.date().future(30, TimeUnit.DAYS)),
                        faker.lorem().sentence(),
                        List.of("GREY")
                },

                {
                        faker.name().firstName(),
                        faker.name().lastName(),
                        faker.address().streetAddress(),
                        faker.number().numberBetween(1, 50),
                        faker.phoneNumber().phoneNumber(),
                        faker.number().numberBetween(1, 10),
                        sdf.format(faker.date().future(30, TimeUnit.DAYS)),
                        faker.lorem().sentence(),
                        List.of("BLACK", "GRAY")
                },
                {
                        faker.name().firstName(),
                        faker.name().lastName(),
                        faker.address().streetAddress(),
                        faker.number().numberBetween(1, 50),
                        faker.phoneNumber().phoneNumber(),
                        faker.number().numberBetween(1, 10),
                        sdf.format(faker.date().future(30, TimeUnit.DAYS)),
                        faker.lorem().sentence(),
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
    public OrderTrack createAnOrder() {
        Response response = OrderApi.createAnOrder(orderData);
        return response.then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .as(OrderTrack.class);
    }

    @Test
    @DisplayName("Create an order")
    @Description("Создание заказа с разными тестовыми данными, проверяется возможность выбора одного цвета, " +
            "двух цветов и возможность не выбирать цвет вообще")
    public void creatingAnOrder() {
        trackNumber = createAnOrder().getNumber();
        assertThat(trackNumber, notNullValue());
    }

    @After
    public void deleteAnOrder() {
        if (trackNumber != 0) {
            Response response = OrderApi.deleteAnOrder(trackNumber);
            response.then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("ok", equalTo(true));

        }

    }

}

