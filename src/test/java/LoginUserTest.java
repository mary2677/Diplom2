import util.*;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import users.*;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static users.UserAndCreds.credsFrom;

public class LoginUserTest {
    private User user;
    private UserAndClient userAndClient = new UserAndClient();
    private RandomUserGenerator userGenerator = new RandomUserGenerator();
    private String accessToken;

    @Before
    public void setUp() {
        user = new User()
                .setEmail(userGenerator.getEmail())
                .setPassword(userGenerator.getPassword())
                .setName(userGenerator.getName());
        ValidatableResponse response = userAndClient.createUser(user);
        String accessTokenBearer = response.extract().path("accessToken");
        accessToken = accessTokenBearer.split(" ")[1];
    }

    @Test
    @DisplayName("login user")
    @Description("авторизация под существующим пользователем")
    public void loginUserTest() {
        ValidatableResponse response = userAndClient.loginUser(credsFrom(user));
        response
                .body("success", equalTo(true))
                .statusCode(HttpStatus.SC_OK);
        assertEquals("Неверное сообщение",
                user.getEmail(),
                response.extract().path("user.email"));
        assertEquals("Неверное сообщение",
                user.getName(),
                response.extract().path("user.name"));
    }

    @Test
    @DisplayName("login user with incorrect email")
    @Description("авторизация с неверным логином")
    public void loginUserWithIncorrectEmailTest() {
        String incorrectEmail = userGenerator.getEmail();
        user = user.setEmail(incorrectEmail);
        ValidatableResponse incorrectEmailResponse = userAndClient.loginUser(credsFrom(user));
        incorrectEmailResponse
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("login user with incorrect password")
    @Description("авторизация с неверным логином и паролем")
    public void loginUserWithIncorrectPasswordTest() {
        String incorrectPassword = userGenerator.getPassword();
        user = user.setPassword(incorrectPassword);
        ValidatableResponse incorrectPasswordResponse = userAndClient.loginUser(credsFrom(user));
        incorrectPasswordResponse
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userAndClient.deleteUser(accessToken);
        }
    }
}
