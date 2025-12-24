package pages.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WikipediaMainPage {
    private AppiumDriver driver;
    private WebDriverWait wait;

    @AndroidFindBy(id = "org.wikipedia:id/search_container")
    private WebElement searchContainer;

    @AndroidFindBy(accessibility = "Search Wikipedia")
    private WebElement searchButton;

    @AndroidFindBy(id = "org.wikipedia:id/fragment_onboarding_skip_button")
    private WebElement skipButton;

    @AndroidFindBy(id = "org.wikipedia:id/view_card_header_title")
    private WebElement mainPageTitle;

    @AndroidFindBy(id = "org.wikipedia:id/nav_more_container")
    private WebElement moreMenu;

    @AndroidFindBy(id = "org.wikipedia:id/main_drawer_login_button")
    private WebElement loginButton;

    @AndroidFindBy(id = "org.wikipedia:id/main_toolbar_wordmark")
    private WebElement wikipediaLogo;

    public WikipediaMainPage(AppiumDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    public void skipOnboarding() {
        try {
            if (skipButton.isDisplayed()) {
                skipButton.click();
                System.out.println("Onboarding пропущен");
            }
        } catch (Exception e) {
            // Onboarding не найден
        }
    }

    public void clickSearch() {
        try {
            searchButton.click();
        } catch (Exception e) {
            searchContainer.click();
        }
    }

    public boolean isSearchContainerDisplayed() {
        try {
            return searchContainer.isDisplayed() || searchButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void enterSearchQuery(String query) {
        // Этот метод теперь в WikipediaSearchPage
    }

    public boolean isMainPageDisplayed() {
        try {
            return wikipediaLogo.isDisplayed() || mainPageTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void openMoreMenu() {
        moreMenu.click();
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

    public void waitForPageToLoad() {
        try {
            wait.until(ExpectedConditions.visibilityOf(wikipediaLogo));
        } catch (Exception e) {
            // Игнорируем если логотип не найден
        }
    }
}