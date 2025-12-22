package pages.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import java.time.Duration;

public class WikipediaArticlePage {
    private AppiumDriver driver;

    @AndroidFindBy(id = "org.wikipedia:id/view_page_title_text")
    private WebElement articleTitle;

    @AndroidFindBy(id = "org.wikipedia:id/view_page_subtitle_text")
    private WebElement articleSubtitle;

    @AndroidFindBy(id = "org.wikipedia:id/page_contents_container")
    private WebElement articleContent;

    @AndroidFindBy(id = "org.wikipedia:id/page_toolbar_button_show_toc")
    private WebElement tableOfContentsButton;

    public WikipediaArticlePage(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    public String getArticleTitle() {
        try {
            return articleTitle.getText();
        } catch (Exception e) {
            System.out.println("Не удалось получить заголовок статьи: " + e.getMessage());
            return "";
        }
    }

    public String getArticleSubtitle() {
        try {
            return articleSubtitle.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isArticleContentDisplayed() {
        try {
            return articleContent.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void openTableOfContents() {
        try {
            tableOfContentsButton.click();
        } catch (Exception e) {
            System.out.println("Не удалось открыть оглавление: " + e.getMessage());
        }
    }

    public void scrollDown() {
        try {
            // Простой способ скролла через JavaScript/Appium
            Dimension size = driver.manage().window().getSize();
            int startX = size.width / 2;
            int startY = (int) (size.height * 0.7);
            int endY = (int) (size.height * 0.3);

            // Используем простой скролл
            driver.executeScript("mobile: scrollGesture",
                    java.util.Map.of(
                            "left", startX, "top", startY, "width", 100, "height", 100,
                            "direction", "down",
                            "percent", 1.0
                    ));

            System.out.println("Скролл выполнен");
        } catch (Exception e) {
            System.out.println("Не удалось выполнить скролл: " + e.getMessage());
            // Альтернатива: просто ждем
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}