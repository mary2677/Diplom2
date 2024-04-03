package util;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.ArrayList;

public class RandomOrderGenerator {
    Faker faker = new Faker();
    private int amountLimit;
    private int min;
    private int max;
    private int randomIndex;
    private ArrayList<String> availableIds;
    private ArrayList<String> orderIds;

    @Step("Получение корректных хэшей ингредиентов для заказа")
    public ArrayList<String> getCorrectOrderIds(ArrayList<String> availableIds, int amount) {
        orderIds = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            randomIndex = faker.number().numberBetween(0, availableIds.size()-1);
            orderIds.add(availableIds.get(randomIndex));
        }
        return orderIds;
    }

    @Step("Получение фэйковых хэшей ингредиентов для заказа")
    public ArrayList<String> getFakeOrderIds(ArrayList<String> availableIds, int amount) {
        orderIds = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            orderIds.add(RandomStringUtils.random(24, true, true));
        }
        return orderIds;
    }

    @Step("Получение хэшей ингредиентов для заказа")
    public ArrayList<String> getOrderIds(ArrayList<String> availableIds, int amount, boolean isCorrect) {
        if (isCorrect) {
            getCorrectOrderIds(availableIds, amount);
        } else {
            getFakeOrderIds(availableIds, amount);
        }
        System.out.println(orderIds.toString());
        return orderIds;
    }
}
