import util.*;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import orders.*;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import users.*;
import java.util.ArrayList;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private User user;
    private UserAndClient userAndClient = new UserAndClient();
    private RandomUserGenerator userGenerator = new RandomUserGenerator();
    private Order order;
    private OrderAndClient orderClient = new OrderAndClient();
    private RandomOrderGenerator orderGenerator = new RandomOrderGenerator();
    private String accessToken;
    private ArrayList<String> availableIds;
    private int amountIds;
    private boolean isCorrect;
    private int expectedStatusCode;

    public CreateOrderTest(int amountIds, boolean isCorrect, int expectedStatusCode) {
        this.amountIds = amountIds;
        this.isCorrect = isCorrect;
        this.expectedStatusCode = expectedStatusCode;
    }

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
    }

    @Parameterized.Parameters(name = "Кол-во хэшей {0}, корректность {1}, статус-код {2}")
    public static Object[][] getUserInfoData() {
        return new Object[][]{
                {2, true, HttpStatus.SC_OK}, //с корректными хэшами
                {3, false, HttpStatus.SC_INTERNAL_SERVER_ERROR}, //с фэйковыми хэшами
                {0, true, HttpStatus.SC_BAD_REQUEST}, //без
        };
    }

    @Test
    @DisplayName("create order with authorization")
    @Description("Создание заказа с авторизацией")
    public void createOrderWithAuthTest() {
        order = new Order(orderGenerator.getOrderIds(availableIds, amountIds, isCorrect));
        ValidatableResponse response = orderClient.createOrder(order, accessToken);
        response
                .statusCode(expectedStatusCode);

    }

    @Test
    @DisplayName("create order without authorization")
    @Description("Создание заказа  без авторизации")
    public void createOrderWithoutAuthTest() {
        order = new Order(orderGenerator.getOrderIds(availableIds, amountIds, isCorrect));
        ValidatableResponse response = orderClient.createOrder(order, "");
        response
                .statusCode(expectedStatusCode);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userAndClient.deleteUser(accessToken);
        }
    }

}
