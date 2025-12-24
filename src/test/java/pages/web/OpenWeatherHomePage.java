package pages.web;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.*;
import utils.ConfigReader;
import java.time.Duration;
import java.util.List;

public class OpenWeatherHomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final ConfigReader config;

    @FindBy(css = "nav, header, .navbar, .main-nav")
    private WebElement navigation;

    public OpenWeatherHomePage(WebDriver driver) {
        this.driver = driver;
        this.config = new ConfigReader();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public boolean isPageLoaded() {
        try {
            String title = driver.getTitle();
            return title != null && !title.isEmpty() &&
                    (title.contains("Weather") || title.contains("OpenWeather"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNavigationDisplayed() {
        try {

            if (navigation != null && navigation.isDisplayed()) {
                return true;
            }

            List<WebElement> navElements = driver.findElements(
                    By.cssSelector("nav, .navbar, header, .header, [role='navigation']")
            );

            for (WebElement nav : navElements) {
                if (nav.isDisplayed()) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            return false;
        }
    }

    public void openHomePage() {
        driver.get(config.getWebBaseUrl());
        waitForPageLoad();
    }

    public void waitForPageLoad() {
        try {
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));
        } catch (Exception e) {
            System.out.println("Игнорируем");
        }
    }
}