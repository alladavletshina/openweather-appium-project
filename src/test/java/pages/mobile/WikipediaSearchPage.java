package pages.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WikipediaSearchPage {
    private AppiumDriver driver;
    private WebDriverWait wait;

    @AndroidFindBy(id = "org.wikipedia:id/search_src_text")
    private WebElement searchInput;

    @AndroidFindBy(id = "org.wikipedia:id/page_list_item_title")
    private List<WebElement> searchResults;

    @AndroidFindBy(id = "org.wikipedia:id/search_empty_message")
    private WebElement emptySearchMessage;

    @AndroidFindBy(id = "org.wikipedia:id/search_results_list")
    private WebElement searchResultsList;

    @AndroidFindBy(id = "org.wikipedia:id/search_close_btn")
    private WebElement closeButton;

    public WikipediaSearchPage(AppiumDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    public void searchFor(String query) {
        wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        searchInput.clear();
        searchInput.sendKeys(query);
    }

    public void clearSearch() {
        searchInput.clear();
    }

    public void selectFirstResult() {
        if (!searchResults.isEmpty()) {
            searchResults.get(0).click();
        }
    }

    public void selectResultByIndex(int index) {
        if (index < searchResults.size()) {
            searchResults.get(index).click();
        }
    }

    public int getSearchResultsCount() {
        return searchResults.size();
    }

    public boolean areSearchResultsDisplayed() {
        try {
            return searchResultsList.isDisplayed() && !searchResults.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmptySearchMessageDisplayed() {
        try {
            return emptySearchMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getFirstResultTitle() {
        if (!searchResults.isEmpty()) {
            return searchResults.get(0).getText();
        }
        return "";
    }

    public List<WebElement> getAllSearchResults() {
        return searchResults;
    }

    public void waitForResults() {
        wait.until(ExpectedConditions.visibilityOf(searchResultsList));
    }

    public void closeSearch() {
        try {
            closeButton.click();
        } catch (Exception e) {
            // Если кнопка закрытия не найдена
        }
    }
}