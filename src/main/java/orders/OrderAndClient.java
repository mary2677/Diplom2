package orders;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import java.util.ArrayList;
import static endpoint.Endpoints.*;
import static io.restassured.RestAssured.given;

public class OrderAndClient {

    private Order order;
    private ValidatableResponse ingredientsResponse;
    private ArrayList<String> availableIds;
    private ArrayList<String> orderIds;

    public OrderAndClient() {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Получение данных об ингредиентах")
    public ValidatableResponse getIngredients() {
        return given()
                .when()
                .get(INGREDIENTS_PATH)
                .then();
    }

    @Step("Получение хэшей доступных ингредиентов")
    public ArrayList<String> getAvailableIds() {
        return getIngredients().extract().path("data._id");
    }

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Получение заказа авторизированного пользователя")
    public ValidatableResponse getOrdersFromUser(String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .when()
                .get(ORDERS_PATH)
                .then();
    }

    @Step("Получение хэшей заказов")
    public ArrayList<String> getAvailableIds(String accessToken) {
        return getOrdersFromUser(accessToken).extract().path("orders._id");
    }
}
