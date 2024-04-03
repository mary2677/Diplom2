import util.*;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import orders.*;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import users.*;
import java.util.ArrayList;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class GetOrdersByUserTest {
    private User user;
    private UserAndClient userAndClient = new UserAndClient();
    private RandomUserGenerator userGenerator = new RandomUserGenerator();
    private Order order;
    private OrderAndClient orderClient = new OrderAndClient();
    private RandomOrderGenerator orderGenerator = new RandomOrderGenerator();
    private String accessToken;
    private ArrayList<String> availableIds;
    private ArrayList<String> expectedOrderIds;
    private ArrayList<String> actualOrderIds;

    @Before
    public void setUp() {
        user = new User()
                .setEmail(userGenerator.getEmail())
                .setPassword(userGenerator.getPassword())
                .setName(userGenerator.getName());
        ValidatableResponse response = userAndClient.createUser(user);
        String accessTokenBearer = response.extract().path("accessToken");
        accessToken = accessTokenBearer.split(" ")[1];
        availableIds = orderClient.getAvailableIds();
        expectedOrderIds = orderGenerator.getOrderIds(availableIds, 2, true);
        order = new Order(expectedOrderIds);
        ValidatableResponse orderResponse = orderClient.createOrder(order, accessToken);
    }

    @Test
    @DisplayName("get orders with auth")
    @Description("Создание заказа с авторизацией")
    public void getOrdersWithAuthTest() {
        ValidatableResponse getOrdersResponse = orderClient.getOrdersFromUser(accessToken);
        getOrdersResponse.statusCode(200);
        actualOrderIds = getOrdersResponse.extract().path("orders[0].ingredients");
        assertEquals("Неверное сообщение",
                expectedOrderIds,
                actualOrderIds);
    }

    @Test
    @DisplayName("get orders without auth")
    @Description("Создание заказа без авторизации")
    public void getOrdersWithoutAuthTest() {
        ValidatableResponse getOrdersResponse = orderClient.getOrdersFromUser("");
        getOrdersResponse
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userAndClient.deleteUser(accessToken);
        }
    }
}
