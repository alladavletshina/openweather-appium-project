package pages.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class OpenWeatherAPIPage {
    private WebDriver driver;

    @FindBy(css = ".api h1")
    private WebElement apiPageHeader;

    @FindBy(linkText = "API doc")
    private WebElement apiDocLink;

    @FindBy(linkText = "How to start")
    private WebElement howToStartLink;

    @FindBy(css = ".api-doc-container")
    private WebElement apiDocContainer;

    public OpenWeatherAPIPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getApiPageHeader() {
        return apiPageHeader.getText();
    }

    public void clickApiDoc() {
        apiDocLink.click();
    }

    public void clickHowToStart() {
        howToStartLink.click();
    }

    public boolean isApiDocDisplayed() {
        return apiDocContainer.isDisplayed();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}