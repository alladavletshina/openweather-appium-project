// src/test/java/base/WebBaseTest.java
package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import utils.ConfigReader;
import utils.DriverManager;
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

        // Устанавливаем таймауты
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getWebTimeout()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        // Создаем явные ожидания
        wait = new WebDriverWait(driver, Duration.ofSeconds(config.getWebTimeout()));

        // Максимизируем окно
        driver.manage().window().maximize();

        System.out.println("✅ Драйвер инициализирован");
        System.out.println("🌐 Браузер: " + config.getWebBrowser());
        System.out.println("🔗 Базовый URL: " + config.getWebBaseUrl());

        // ОТКРЫВАЕМ ГЛАВНУЮ СТРАНИЦУ ЗДЕСЬ!
        System.out.println("🌐 Открываем главную страницу...");
        driver.get(config.getWebBaseUrl());
        waitForPageLoad();
        System.out.println("✅ Главная страница открыта");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            try {
                // Закрываем драйвер аккуратно
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
        System.out.println("\n--- Начало нового теста ---");
    }

    @AfterMethod
    public void afterMethod() {
        System.out.println("--- Конец теста ---\n");
    }

    // Вспомогательный метод для ожидания загрузки страницы
    protected void waitForPageLoad() {
        try {
            wait.until(d -> {
                String readyState = (String) ((org.openqa.selenium.JavascriptExecutor) d)
                        .executeScript("return document.readyState");
                return "complete".equals(readyState);
            });
            System.out.println("📄 Страница загружена");
        } catch (Exception e) {
            System.out.println("⚠️ Ошибка при ожидании загрузки страницы: " + e.getMessage());
        }
    }

    // Универсальный метод для короткого ожидания
    protected void waitFor(int seconds) {
        try {
            System.out.println("⏳ Ожидание " + seconds + " секунд...");
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Метод для возврата на главную страницу
    protected void goToHomePage() {
        System.out.println("🏠 Возвращаемся на главную страницу...");
        driver.get(config.getWebBaseUrl());
        waitForPageLoad();
    }
}