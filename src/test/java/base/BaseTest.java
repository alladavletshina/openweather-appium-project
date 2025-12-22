package base;

public abstract class BaseTest {
    // Простой базовый класс без проблемных импортов
    protected void log(String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        System.out.println("[" + timestamp + "] " + message);
    }
}