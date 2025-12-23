package pages.web;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import org.openqa.selenium.support.ui.*;
import utils.ConfigReader;
import java.time.Duration;
import java.util.List;

public class OpenWeatherHomePage {
    private WebDriver driver;
    private WebDriverWait wait;
    private ConfigReader config;

    // ИСПРАВЛЕННЫЕ РАБОЧИЕ ЛОКАТОРЫ
    @FindBy(css = "input[placeholder='Search city'], input.search-input, #search_str")
    private WebElement searchInput;

    @FindBy(css = "button.btn-search, button[type='submit'], .search-button")
    private WebElement searchButton;

    @FindBy(css = "nav, header, .navbar, .main-nav")
    private WebElement navigation;

    @FindBy(css = "footer, .footer")
    private WebElement footer;

    @FindBy(tagName = "body")
    private WebElement pageBody;

    @FindBy(css = ".weather-items, .current-container, .weather-widget")
    private WebElement weatherWidget;

    @FindBy(css = "div.search-dropdown-menu li, .search-results li")
    private List<WebElement> searchResults;

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

    public void searchForCity(String cityName) {
        try {
            // Способ 1: Используем PageFactory элементы
            if (searchInput != null && searchInput.isDisplayed()) {
                wait.until(ExpectedConditions.elementToBeClickable(searchInput));
                searchInput.clear();
                searchInput.sendKeys(cityName);

                if (searchButton != null && searchButton.isDisplayed()) {
                    searchButton.click();
                } else {
                    searchInput.sendKeys(Keys.ENTER);
                }

            } else {
                // Способ 2: Используем JavaScript для поиска поля
                searchWithJavaScript(cityName);
            }

            // Ждем результаты
            Thread.sleep(2000);

        } catch (Exception e) {
            System.out.println("Поиск не сработал, используем прямой URL: " + e.getMessage());
            // Способ 3: Прямой URL
            driver.get(config.getWebBaseUrl() + "/find?q=" + cityName);
        }

        waitForPageLoad();
    }

    private void searchWithJavaScript(String cityName) {
        try {
            String script =
                    "var inputs = document.getElementsByTagName('input');" +
                            "for(var i = 0; i < inputs.length; i++) {" +
                            "   var input = inputs[i];" +
                            "   if(input.type === 'text' || input.placeholder || input.className.includes('search')) {" +
                            "       input.value = '" + cityName + "';" +
                            "       input.dispatchEvent(new Event('input'));" +
                            "       break;" +
                            "   }" +
                            "}" +
                            "var forms = document.getElementsByTagName('form');" +
                            "for(var i = 0; i < forms.length; i++) {" +
                            "   if(forms[i].className.includes('search') || forms[i].getAttribute('action')) {" +
                            "       forms[i].submit();" +
                            "       break;" +
                            "   }" +
                            "}";

            ((JavascriptExecutor) driver).executeScript(script);

        } catch (Exception e) {
            System.out.println("JavaScript search failed: " + e.getMessage());
        }
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean isNavigationDisplayed() {
        try {
            // Проверяем несколько способов
            if (navigation != null && navigation.isDisplayed()) {
                return true;
            }

            // Ищем навигацию через другие селекторы
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

    public boolean isFooterDisplayed() {
        try {
            return footer != null && footer.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isWeatherWidgetDisplayed() {
        try {
            return weatherWidget != null && weatherWidget.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void navigateTo(String url) {
        String baseUrl = config.getWebBaseUrl();
        driver.get(baseUrl + url);
        waitForPageLoad();
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
        waitForPageLoad();
    }

    public int getSearchResultsCount() {
        try {
            // Ждем появления результатов
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.search-dropdown-menu, .search-results, .results-container")
            ));
            return searchResults.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<WebElement> getSearchResults() {
        return searchResults;
    }

    public void waitForPageLoad() {
        try {
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));
        } catch (Exception e) {
            // Игнорируем
        }
    }
}