package base;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import utils.*;
import java.time.Duration;

public class WebBaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected ConfigReader config;

    @BeforeClass
    public void setUp() {
        System.out.println("🌐 НАСТРОЙКА ВЕБ-ТЕСТОВ ДЛЯ OPENWEATHERMAP");
        System.out.println("==============================================");

        config = new ConfigReader();
        driver = DriverManager.getWebDriver();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getWebTimeout()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        wait = new WebDriverWait(driver, Duration.ofSeconds(config.getWebTimeout()));

        driver.manage().window().maximize();

        System.out.println("✅ Драйвер инициализирован");
        System.out.println("🌐 Браузер: " + config.getWebBrowser());
        System.out.println("🔗 Базовый URL: " + config.getWebBaseUrl());
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            try {
                Thread.sleep(1000);
                DriverManager.closeDriver();
                System.out.println("✅ Драйвер закрыт");
            } catch (Exception e) {
                System.out.println("⚠️ Ошибка при закрытии драйвера: " + e.getMessage());
            }
        }
    }

    @BeforeMethod
    public void beforeMethod() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("НАЧАЛО НОВОГО ТЕСТА");
        System.out.println("=".repeat(50));
    }

    @AfterMethod
    public void afterMethod() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ТЕСТ ЗАВЕРШЕН");
        System.out.println("=".repeat(50) + "\n");
    }

    protected void waitForPageLoad() {
        try {
            wait.until(d -> {
                String readyState = (String) ((JavascriptExecutor) d)
                        .executeScript("return document.readyState");
                return "complete".equals(readyState);
            });
        } catch (Exception e) {
            System.out.println("⚠️ Ошибка при ожидании загрузки страницы: " + e.getMessage());
        }
    }

    protected void waitFor() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}