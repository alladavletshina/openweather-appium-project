package pages.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;

public class OpenWeatherSearchPage {
    private WebDriver driver;

    @FindBy(css = "table td a")
    private List<WebElement> searchResults;

    @FindBy(css = ".search-block")
    private WebElement searchBlock;

    @FindBy(css = "h1")
    private WebElement pageHeader;

    @FindBy(css = ".table-responsive")
    private WebElement resultsTable;

    public OpenWeatherSearchPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickFirstSearchResult() {
        if (!searchResults.isEmpty()) {
            searchResults.get(0).click();
        }
    }

    public int getSearchResultsCount() {
        return searchResults.size();
    }

    public boolean isSearchResultsDisplayed() {
        return searchBlock.isDisplayed() && resultsTable.isDisplayed();
    }

    public String getHeaderText() {
        return pageHeader.getText();
    }

    public boolean areResultsNotEmpty() {
        return !searchResults.isEmpty();
    }
}