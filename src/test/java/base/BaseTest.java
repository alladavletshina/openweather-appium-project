package base;

public abstract class BaseTest {

    protected void log(String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        System.out.println("[" + timestamp + "] " + message);
    }
}