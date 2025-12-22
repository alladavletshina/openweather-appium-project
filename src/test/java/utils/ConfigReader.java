package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private Properties properties;

    public ConfigReader() {
        properties = new Properties();
        try {
            FileInputStream input = new FileInputStream("src/test/resources/config.properties");
            properties.load(input);
            input.close();
        } catch (IOException e) {
            System.err.println("Error loading config.properties: " + e.getMessage());
        }
    }

    // Web methods
    public String getWebBaseUrl() {
        return properties.getProperty("web.base.url", "https://openweathermap.org");
    }

    public String getWebBrowser() {
        return properties.getProperty("web.browser", "chrome");
    }

    public int getWebTimeout() {
        return Integer.parseInt(properties.getProperty("web.timeout", "10"));
    }

    public String getWebApiKey() {
        return properties.getProperty("web.api.key", "demo");
    }

    // Mobile methods
    public String getMobilePlatformName() {
        return properties.getProperty("mobile.platform.name", "Android");
    }

    public String getMobileDeviceName() {
        return properties.getProperty("mobile.device.name", "Pixel_4_API_30");
    }

    public String getMobilePlatformVersion() {
        return properties.getProperty("mobile.platform.version", "11.0");
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

    public String getMobileAppPath() {
        return properties.getProperty("mobile.app.path", "src/test/resources/wikipedia.apk");
    }

    public String getMobileServerUrl() {
        return properties.getProperty("mobile.server.url", "http://127.0.0.1:4723");
    }
}