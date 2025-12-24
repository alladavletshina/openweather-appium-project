package utils;

import org.testng.asserts.SoftAssert;

public class AssertionUtils {
    private static ThreadLocal<SoftAssert> softAssert = new ThreadLocal<>();

    public static SoftAssert getSoftAssert() {
        if (softAssert.get() == null) {
            softAssert.set(new SoftAssert());
        }
        return softAssert.get();
    }

    public static void assertAll() {
        if (softAssert.get() != null) {
            softAssert.get().assertAll();
            softAssert.remove();
        }
    }

    public static void assertElementExists(String elementName, boolean exists, String message) {
        getSoftAssert().assertTrue(exists,
                String.format("%s: %s должен существовать", elementName, message));
    }

    public static void assertTextContains(String actual, String expected, String elementName) {
        getSoftAssert().assertTrue(actual.contains(expected),
                String.format("%s должен содержать '%s'. Фактически: %s",
                        elementName, expected, actual));
    }
}