package pages.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import java.time.Duration;

public class WikipediaMainPage {
    private AppiumDriver driver;

    @AndroidFindBy(id = "org.wikipedia:id/search_container")
    private WebElement searchContainer;

    @AndroidFindBy(id = "org.wikipedia:id/search_src_text")
    private WebElement searchInput;

    @AndroidFindBy(id = "org.wikipedia:id/fragment_onboarding_skip_button")
    private WebElement skipButton;

    @AndroidFindBy(id = "org.wikipedia:id/view_card_header_title")
    private WebElement mainPageTitle;

    @AndroidFindBy(id = "org.wikipedia:id/nav_more_container")
    private WebElement moreMenu;

    @AndroidFindBy(id = "org.wikipedia:id/main_drawer_login_button")
    private WebElement loginButton;

    public WikipediaMainPage(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    public void skipOnboarding() {
        if (skipButton.isDisplayed()) {
            skipButton.click();
        }
    }

    public void clickSearch() {
        searchContainer.click();
    }

    public void enterSearchQuery(String query) {
        searchInput.sendKeys(query);
    }

    public boolean isMainPageDisplayed() {
        return mainPageTitle.isDisplayed();
    }

    public void openMoreMenu() {
        moreMenu.click();
    }

    public boolean isLoginButtonDisplayed() {
        return loginButton.isDisplayed();
    }

    public String getMainPageTitle() {
        return mainPageTitle.getText();
    }
}