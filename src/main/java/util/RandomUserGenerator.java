package util;

import com.github.javafaker.Faker;
import java.util.Locale;

public class RandomUserGenerator {
    Faker fakerRu = new Faker(new Locale("ru"));
    Faker fakerEn = new Faker(new Locale("en"));
    public String getEmail() {
        return fakerEn.internet().emailAddress();
    }

    public String getPassword() {
        return fakerEn.internet().password();
    }

    public String getName() {
        return fakerRu.name().name();
    }
}
