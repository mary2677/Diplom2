package users;

import io.qameta.allure.Step;

public class UserAndCreds {
    private String email;
    private String password;

    public UserAndCreds(String login, String password) {
        this.email = login;
        this.password = password;
    }

    @Step("Получение учетных данных пользователя")
    public static UserAndCreds credsFrom(User user) {
        return new UserAndCreds(user.getEmail(), user.getPassword());
    }
}
