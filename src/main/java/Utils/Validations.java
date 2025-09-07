package Utils;

import io.qameta.allure.Step;
import org.testng.Assert;

public class Validations {
    private Validations() {
    }

    @Step("Validate true")
    public static void validateTrue(boolean condition, String message) {
        Assert.assertTrue(condition, message);

    }

    @Step("Validate false")
    public static void validateFalse(boolean condition, String message) {
        Assert.assertFalse(condition, message);

    }

    @Step("Validate equal ")
    public static void validateEquals(String actual, String expected, String message) {
        Assert.assertEquals(actual, expected, message);

    }

    @Step("Validate equal ")
    public static void validateEquals(int actual, int expected, String message) {
        Assert.assertEquals(actual, expected, message);

    }

    @Step("Validate not equal ")
    public static void validateNotEquals(String actual, String expected, String message) {
        Assert.assertNotEquals(actual, expected, message);

    }


}
