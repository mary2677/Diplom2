package users;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import static endpoint.Endpoints.*;
import static io.restassured.RestAssured.given;

public class UserAndClient {

    private User user;
    private String accessToken;
    private static UserAndCreds userAndCreds;

    public UserAndClient() {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(REGISTER_PATH)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(UserAndCreds userAndCreds) {
        return given()
                .header("Content-type", "application/json")
                .body(userAndCreds)
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    @Step("получение данных о пользователе")
    public ValidatableResponse getUser(String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .get(USER_PATH)
                .then();
    }

    @Step("обновление данных о пользователе")
    public ValidatableResponse patchUser(String newUserInfo, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .body(newUserInfo)
                .when()
                .patch(USER_PATH)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .delete(USER_PATH)
                .then();
    }
}
