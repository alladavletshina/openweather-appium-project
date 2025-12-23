// src/test/java/utils/DriverManager.java
package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverManager {
    private static WebDriver driver;
    private static ConfigReader config = new ConfigReader();

    public static WebDriver getWebDriver() {
        if (driver == null) {
            String browser = config.getWebBrowser().toLowerCase();

            switch (browser) {
                case "chrome":
                    driver = createChromeDriver();
                    break;
                case "firefox":
                    driver = createFirefoxDriver();
                    break;
                case "edge":
                    driver = createEdgeDriver();
                    break;
                default:
                    throw new IllegalArgumentException("Неподдерживаемый браузер: " + browser);
            }

            // Общие настройки драйвера
            setupDriverCommonSettings(driver);
        }
        return driver;
    }

    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--remote-allow-origins=*");

        // Отключаем сообщения "Chrome is being controlled"
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        // Отключаем сохранение паролей
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--start-maximized");

        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();

        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        return new EdgeDriver(options);
    }

    private static void setupDriverCommonSettings(WebDriver driver) {
        // Устанавливаем таймауты
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getWebTimeout()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        // Максимизируем окно (дополнительно)
        driver.manage().window().maximize();
    }

    public static void closeDriver() {
        if (driver != null) {
            try {
                driver.quit();
                driver = null;
                System.out.println("🚪 Драйвер успешно закрыт");
            } catch (Exception e) {
                System.err.println("❌ Ошибка при закрытии драйвера: " + e.getMessage());
            }
        }
    }

    public static WebDriver getCurrentDriver() {
        return driver;
    }
}