package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
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
    private static final ConfigReader config = new ConfigReader();

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

            setupDriverCommonSettings(driver);
        }
        return driver;
    }

    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // УБИРАЕМ автоматическое открытие в полноэкранном режиме
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--remote-allow-origins=*");

        // УСКОРЯЕМ загрузку - отключаем некоторые функции
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // УСТАНАВЛИВАЕМ языковые настройки
        options.addArguments("--lang=en-US");

        // ИГНОРИРУЕМ ошибки сертификатов (для тестов)
        options.setAcceptInsecureCerts(true);

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        // ОТКЛЮЧАЕМ предупреждения безопасности
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.setAcceptInsecureCerts(true);

        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();

        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--inprivate"); // Режим инкогнито
        options.setAcceptInsecureCerts(true);

        return new EdgeDriver(options);
    }

    private static void setupDriverCommonSettings(WebDriver driver) {
        // УСТАНАВЛИВАЕМ ТАЙМАУТЫ из конфига
        int pageLoadTimeout = config.getWebPageLoadTimeout();
        int implicitWait = config.getWebImplicitWait();
        int scriptTimeout = 30;

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout)); // УВЕЛИЧИВАЕМ
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(scriptTimeout));

        // МЕНЬШЕ РИСКОВ: не максимизируем окно сразу
        // driver.manage().window().maximize();

        System.out.println("⏱️  Установлены таймауты:");
        System.out.println("   • Page Load: " + pageLoadTimeout + " сек");
        System.out.println("   • Implicit: " + implicitWait + " сек");
        System.out.println("   • Script: " + scriptTimeout + " сек");
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

    // НОВЫЙ МЕТОД: перезагрузка драйвера
    public static void restartDriver() {
        if (driver != null) {
            closeDriver();
        }
        driver = null;
        getWebDriver();
    }
}