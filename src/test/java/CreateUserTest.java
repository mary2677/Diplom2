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

public class CreateUserTest {
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
    }

    @Test
    @DisplayName("create new user")
    @Description("Создание нового пользователя")
    public void createNewUser() {
        ValidatableResponse response = userAndClient.createUser(user);
        String accessTokenBearer = response.extract().path("accessToken");
        accessToken = accessTokenBearer.split(" ")[1];
        response
                .body("success", equalTo(true))
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("create two equal users")
    @Description("Создание пользователя, который уже зарегистрирован")
    public void createTwoEqualUsers() {
        ValidatableResponse response1 = userAndClient.createUser(user);
        String accessTokenBearer = response1.extract().path("accessToken");
        accessToken = accessTokenBearer.split(" ")[1];
        ValidatableResponse response2 = userAndClient.createUser(user);
        response2
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"))
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("create user without email")
    @Description("Создание пользователя без поле email")
    public void createUserWithoutEmail() {
        user = user.setEmail("");
        ValidatableResponse response = userAndClient.createUser(user);
        response
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("create user without password")
    @Description("Создание пользователя без поле password")
    public void createUserWithoutPassword() {
        user = user.setPassword("");
        ValidatableResponse response = userAndClient.createUser(user);
        response
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("create user without name")
    @Description("Создание пользователя без поле name")
    public void createUserWithoutName() {
        user = user.setName("");
        ValidatableResponse response = userAndClient.createUser(user);
        response
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userAndClient.deleteUser(accessToken);
        }
    }
}
