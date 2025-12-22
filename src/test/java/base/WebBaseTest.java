package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import utils.ConfigReader;
import utils.DriverManager;
import utils.WaitUtils;
import java.time.Duration;

public class WebBaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected ConfigReader config;

    @BeforeClass
    public void setUp() {
        config = new ConfigReader();
        driver = DriverManager.getWebDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(config.getWebTimeout()));
        driver.manage().window().maximize();
        driver.get(config.getWebBaseUrl());
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            try {
                // Закрываем драйвер более аккуратно
                driver.close();
                Thread.sleep(500);
            } catch (Exception e) {
                // Игнорируем ошибки закрытия
            } finally {
                driver.quit();
            }
        }
    }
}