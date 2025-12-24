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
    protected JavascriptExecutor js;

    @BeforeClass
    public void setUp() {
        System.out.println("🌐 НАСТРОЙКА ВЕБ-ТЕСТОВ ДЛЯ OPENWEATHERMAP");
        System.out.println("==============================================");

        config = new ConfigReader();
        driver = DriverManager.getWebDriver();

        // Инициализируем JavascriptExecutor
        js = (JavascriptExecutor) driver;

        // УСТАНАВЛИВАЕМ увеличенные таймауты
        int timeout = config.getWebTimeout();
        int pageLoadTimeout = config.getWebPageLoadTimeout();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout)); // УВЕЛИЧЕНО
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        // МАКСИМИЗИРУЕМ ПОЗЖЕ, после загрузки первой страницы
        // driver.manage().window().maximize();

        System.out.println("✅ Драйвер инициализирован");
        System.out.println("🌐 Браузер: " + config.getWebBrowser());
        System.out.println("🔗 Базовый URL: " + config.getWebBaseUrl());
        System.out.println("⏱️  Таймауты: implicit=" + timeout + "s, pageLoad=" + pageLoadTimeout + "s");
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

        // Делаем небольшую паузу между тестами
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void waitForPageLoad() {
        try {
            // Ждём загрузки DOM
            wait.until(d -> {
                String readyState = (String) ((JavascriptExecutor) d)
                        .executeScript("return document.readyState");
                return "complete".equals(readyState) || "interactive".equals(readyState);
            });

            // Дополнительная проверка: ждём появления body
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            System.out.println("   ✓ Страница загружена (readyState: complete)");
        } catch (Exception e) {
            System.out.println("⚠️ Ошибка при ожидании загрузки страницы: " + e.getMessage());
            System.out.println("   Продолжаем выполнение...");
        }
    }

    // НОВЫЙ МЕТОД: умное ожидание с повторными попытками
    protected void waitForPageLoadWithRetry(int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                waitForPageLoad();
                return; // Успех
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    throw e; // Последняя попытка
                }
                System.out.println("   ↻ Повторная попытка загрузки (" + (i+1) + "/" + maxRetries + ")");
                waitForSeconds(2);
            }
        }
    }

    // НОВЫЙ МЕТОД: проверка видимости элемента
    protected boolean isElementVisible(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // НОВЫЙ МЕТОД: безопасный клик
    protected void safeClick(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
            System.out.println("   ✓ Клик выполнен: " + locator);
        } catch (Exception e) {
            System.out.println("   ⚠️ Не удалось кликнуть: " + locator);
            throw e;
        }
    }

    // НОВЫЙ МЕТОД: проверка заголовка страницы
    protected boolean isPageTitleContains(String expectedText) {
        try {
            String actualTitle = driver.getTitle();
            return actualTitle != null && actualTitle.contains(expectedText);
        } catch (Exception e) {
            return false;
        }
    }

    protected void waitFor() {
        try {
            Thread.sleep(2000L); // Увеличиваем паузу
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // НОВЫЙ МЕТОД: ожидание с параметром
    protected void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}