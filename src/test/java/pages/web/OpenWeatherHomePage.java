package pages.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;
import java.time.Duration;

public class OpenWeatherHomePage {
    private WebDriver driver;
    private WebDriverWait wait;
    private ConfigReader config;

    // Более гибкие локаторы
    @FindBy(css = "input[placeholder*='Search'], input[placeholder*='search']")
    private WebElement searchInput;

    @FindBy(css = "button[type='submit'], .search-button")
    private WebElement searchButton;

    @FindBy(css = "nav, .navbar, header")
    private WebElement navigation;

    @FindBy(css = "footer, .footer")
    private WebElement footer;

    @FindBy(tagName = "body")
    private WebElement pageBody;

    public OpenWeatherHomePage(WebDriver driver) {
        this.driver = driver;
        this.config = new ConfigReader();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public boolean isPageLoaded() {
        try {
            return pageBody.isDisplayed() && driver.getTitle() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void searchForCity(String cityName) {
        try {
            // Ждем пока поле поиска станет доступным
            if (searchInput != null) {
                wait.until(ExpectedConditions.elementToBeClickable(searchInput));

                searchInput.clear();
                searchInput.sendKeys(cityName);

                // Пробуем кликнуть, если не получается - нажимаем Enter
                try {
                    if (searchButton != null && searchButton.isDisplayed() && searchButton.isEnabled()) {
                        searchButton.click();
                    } else {
                        searchInput.sendKeys(org.openqa.selenium.Keys.ENTER);
                    }
                } catch (Exception e) {
                    searchInput.sendKeys(org.openqa.selenium.Keys.ENTER);
                }

                // Ждем обновления страницы
                Thread.sleep(2000);
            }

        } catch (Exception e) {
            System.out.println("Error in search: " + e.getMessage());
            // Если не нашли элементы, используем JavaScript
            searchWithJavaScript(cityName);
        }
    }

    private void searchWithJavaScript(String cityName) {
        try {
            // Альтернативный способ через JavaScript
            String script =
                    "var inputs = document.querySelectorAll('input'); " +
                            "for(var i=0; i<inputs.length; i++) { " +
                            "  if(inputs[i].type === 'text' || (inputs[i].placeholder && inputs[i].placeholder.toLowerCase().includes('search'))) { " +
                            "    inputs[i].value = '" + cityName + "'; " +
                            "    inputs[i].dispatchEvent(new Event('input', { bubbles: true })); " +
                            "    break; " +
                            "  } " +
                            "} " +
                            "setTimeout(function() { " +
                            "  var event = new KeyboardEvent('keydown', { key: 'Enter', code: 'Enter', keyCode: 13 }); " +
                            "  document.dispatchEvent(event); " +
                            "}, 100);";

            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(script);
            Thread.sleep(2000);

        } catch (Exception e) {
            System.out.println("JavaScript search also failed: " + e.getMessage());
        }
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean isNavigationDisplayed() {
        try {
            return navigation != null && navigation.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFooterDisplayed() {
        try {
            return footer != null && footer.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void navigateTo(String url) {
        String baseUrl = config.getWebBaseUrl();
        if (!baseUrl.endsWith("/") && !url.startsWith("/")) {
            url = "/" + url;
        }
        driver.get(baseUrl + url);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isSearchInputDisplayed() {
        try {
            return searchInput != null && searchInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void openHomePage() {
        driver.get(config.getWebBaseUrl());
    }
}