package pages.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class WikipediaMainPage {

    @AndroidFindBy(id = "org.wikipedia:id/search_container")
    private WebElement searchContainer;

    @AndroidFindBy(accessibility = "Search Wikipedia")
    private WebElement searchButton;

    @AndroidFindBy(id = "org.wikipedia:id/view_card_header_title")
    private WebElement mainPageTitle;

    @AndroidFindBy(id = "org.wikipedia:id/main_drawer_login_button")
    private WebElement loginButton;

    @AndroidFindBy(id = "org.wikipedia:id/main_toolbar_wordmark")
    private WebElement wikipediaLogo;

    public WikipediaMainPage(AppiumDriver driver) {
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    public void clickSearch() {
        try {
            searchButton.click();
        } catch (Exception e) {
            searchContainer.click();
        }
    }


    public boolean isMainPageDisplayed() {
        try {
            return wikipediaLogo.isDisplayed() || mainPageTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoginButtonDisplayed() {
        try {
            return loginButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getMainPageTitle() {
        try {
            return mainPageTitle.getText();
        } catch (Exception e) {
            return "";
        }
    }

}