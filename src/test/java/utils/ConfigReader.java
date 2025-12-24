package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private final Properties properties;

    public ConfigReader() {
        properties = new Properties();
        try {

            InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");

            if (input == null) {

                input = new FileInputStream("src/test/resources/config.properties");
            }

            properties.load(input);
            input.close();
            System.out.println("✅ Конфигурационный файл загружен");
        } catch (IOException e) {
            System.err.println("❌ Ошибка загрузки config.properties: " + e.getMessage());
            setDefaultProperties();
        }
    }

    private void setDefaultProperties() {
        System.out.println("⚙️ Используются значения по умолчанию");

        properties.setProperty("web.base.url", "https://openweathermap.org");
        properties.setProperty("web.browser", "chrome");
        properties.setProperty("web.timeout", "10");
        properties.setProperty("web.api.key", "demo");

        properties.setProperty("mobile.platform.name", "Android");
        properties.setProperty("mobile.platform.version", "14.0");
        properties.setProperty("mobile.device.name", "Medium_Phone_API_36.1");
        properties.setProperty("mobile.automation.name", "UiAutomator2");
        properties.setProperty("mobile.app.package", "org.wikipedia");
        properties.setProperty("mobile.app.activity", "org.wikipedia.main.MainActivity");
        properties.setProperty("mobile.app.path", "src/test/resources/wikipedia.apk");
        properties.setProperty("mobile.server.url", "http://127.0.0.1:4723");
    }

    public String getWebBaseUrl() {
        return properties.getProperty("web.base.url", "https://openweathermap.org");
    }

    public String getWebBrowser() {
        return properties.getProperty("web.browser", "chrome");
    }

    public int getWebTimeout() {
        try {
            return Integer.parseInt(properties.getProperty("web.timeout", "10"));
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    public String getMobilePlatformName() {
        return properties.getProperty("mobile.platform.name", "Android");
    }

    public String getMobileDeviceName() {
        return properties.getProperty("mobile.device.name", "Pixel_4_API_30");
    }

    public String getMobileAutomationName() {
        return properties.getProperty("mobile.automation.name", "UiAutomator2");
    }

    public String getMobileAppPackage() {
        return properties.getProperty("mobile.app.package", "org.wikipedia");
    }

    public String getMobileAppActivity() {
        return properties.getProperty("mobile.app.activity", "org.wikipedia.main.MainActivity");
    }

    public String getMobileServerUrl() {
        return properties.getProperty("mobile.server.url", "http://127.0.0.1:4723");
    }
}