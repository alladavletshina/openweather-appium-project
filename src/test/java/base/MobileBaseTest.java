package base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import utils.ConfigReader;

import java.net.URL;
import java.time.Duration;

public class MobileBaseTest {
    protected AndroidDriver driver;
    protected ConfigReader config;

    @BeforeClass
    public void setUp() throws Exception {
        System.out.println("📱 НАСТРОЙКА ТЕСТОВ ДЛЯ WIKIPEDIA");
        System.out.println("====================================");

        config = new ConfigReader();

        // Ждем загрузки эмулятора
        System.out.println("⏳ Ожидание загрузки эмулятора...");
        Thread.sleep(10000);

        System.out.println("🔧 Конфигурация:");
        System.out.println("   • Platform: " + config.getMobilePlatformName());
        System.out.println("   • Device: " + config.getMobileDeviceName());
        System.out.println("   • App: Wikipedia");
        System.out.println("   • Server: " + config.getMobileServerUrl());

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(config.getMobilePlatformName())
                .setAutomationName(config.getMobileAutomationName())
                .setDeviceName(config.getMobileDeviceName())
                .setAppPackage(config.getMobileAppPackage())
                .setAppActivity(config.getMobileAppActivity())
                .setNoReset(false)
                .setAutoGrantPermissions(true)
                .setNewCommandTimeout(Duration.ofSeconds(60))
                .setAppWaitDuration(Duration.ofSeconds(30));

        System.out.println("🔗 Подключение к Appium...");
        driver = new AndroidDriver(new URL(config.getMobileServerUrl()), options);

        // Устанавливаем неявные ожидания
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        System.out.println("✅ Драйвер инициализирован");
        System.out.println("   Пакет: " + driver.getCurrentPackage());
        System.out.println("   Активность: " + driver.currentActivity());
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("✅ Драйвер закрыт");
            } catch (Exception e) {
                System.out.println("⚠️ Ошибка при закрытии драйвера: " + e.getMessage());
            }
        }
    }

    // Вспомогательный метод для ожидания
    protected void waitForSeconds(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000L));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}