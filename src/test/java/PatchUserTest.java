import util.*;
import com.google.gson.JsonObject;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import users.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PatchUserTest {
    private User user;
    private UserAndClient userAndClient = new UserAndClient();
    private RandomUserGenerator userGenerator = new RandomUserGenerator();
    JsonObject newUserInfo = new JsonObject();
    private String accessToken;
    private boolean isEmailChanged;
    private boolean isNameChanged;
    String expectedEmail;
    String expectedName;

    public PatchUserTest(boolean isEmailChanged, boolean isNameChanged) {
        this.isEmailChanged = isEmailChanged;
        this.isNameChanged = isNameChanged;
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
    }

    @Parameterized.Parameters(name = "Изменяемые поля: email {0}, name {1}")
    public static Object[][] getUserInfoData() {
        return new Object[][]{
                {true, true},
                {true, false},
                {false, true},
                {false, false},
        };
    }
    @Test
    @DisplayName("patch user with Auth")
    @Description("изменение данных авторизированного пользователя")
    public void patchUserWithAuthTest() {
        if (isEmailChanged) {
            expectedEmail = userGenerator.getEmail();
            newUserInfo.addProperty("email", expectedEmail);
        } else {
            expectedEmail = user.getEmail();
        }
        if (isNameChanged) {
            expectedName = userGenerator.getName();
            newUserInfo.addProperty("name", expectedName);
        } else {
            expectedName = user.getName();
        }
        ValidatableResponse patchUserResponse = userAndClient.patchUser(newUserInfo.toString(), accessToken);
        patchUserResponse
                .body("success", equalTo(true))
                .statusCode(HttpStatus.SC_OK);
        assertEquals("Неверное сообщение",
                expectedEmail,
                patchUserResponse.extract().path("user.email"));
        assertEquals("Неверное сообщение",
                expectedName,
                patchUserResponse.extract().path("user.name"));
    }

    @Test
    @DisplayName("patch user without Auth")
    @Description("изменение данных не авторизированного пользователя")
    public void patchUserWithoutAuthTest() {
        if (isEmailChanged) {
            expectedEmail = userGenerator.getEmail();
            newUserInfo.addProperty("email", expectedEmail);
        } else {
            expectedEmail = user.getEmail();
        }
        if (isNameChanged) {
            expectedName = userGenerator.getName();
            newUserInfo.addProperty("name", expectedName);
        } else {
            expectedName = user.getName();
        }
        ValidatableResponse patchUserResponse = userAndClient.patchUser(newUserInfo.toString(), "");
        patchUserResponse
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
