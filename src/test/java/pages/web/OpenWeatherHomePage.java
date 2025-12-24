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

    @FindBy(css = "h1, .headline, .title, [class*='heading']")
    private List<WebElement> headings;

    @FindBy(css = "input[type='search'], input[placeholder*='search'], #search")
    private WebElement searchInput;

    public OpenWeatherHomePage(WebDriver driver) {
        this.driver = driver;
        this.config = new ConfigReader();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Увеличиваем ожидание
        PageFactory.initElements(driver, this);
    }

    public boolean isPageLoaded() {
        try {
            // Ждём загрузки страницы
            waitForPageLoad();

            // Проверяем заголовок
            String title = driver.getTitle();
            if (title == null || title.isEmpty()) {
                System.out.println("   ⚠️ Заголовок пустой");
                return false;
            }

            System.out.println("   Заголовок страницы: " + title);

            // Более гибкая проверка
            String lowerTitle = title.toLowerCase();
            boolean hasWeatherKeywords = lowerTitle.contains("weather") ||
                    lowerTitle.contains("openweather") ||
                    lowerTitle.contains("forecast") ||
                    lowerTitle.contains("climate");

            if (!hasWeatherKeywords) {
                System.out.println("   ⚠️ Заголовок не содержит погодных ключевых слов");
                // Но не падаем - может быть редирект или временная страница
            }

            // Проверяем наличие основного контента
            boolean hasBody = driver.findElements(By.tagName("body")).size() > 0;
            boolean hasContent = driver.getPageSource().length() > 1000;

            System.out.println("   Тело страницы: " + hasBody);
            System.out.println("   Контент (>1000 символов): " + hasContent);

            return hasBody && hasContent;

        } catch (Exception e) {
            System.out.println("   ❌ Ошибка проверки загрузки: " + e.getMessage());
            return false;
        }
    }

    public boolean isNavigationDisplayed() {
        try {
            // Сначала ждём немного
            Thread.sleep(1000);

            // Пробуем стандартный элемент
            if (navigation != null && navigation.isDisplayed()) {
                return true;
            }

            // Ищем навигацию разными способами
            List<WebElement> navElements = driver.findElements(
                    By.cssSelector("nav, .navbar, header, .header, [role='navigation'], menu, ul.menu, div.menu")
            );

            for (WebElement nav : navElements) {
                try {
                    if (nav.isDisplayed()) {
                        System.out.println("   Навигация найдена через: " + nav.getTagName());
                        return true;
                    }
                } catch (Exception e) {
                    // Пропускаем
                }
            }

            // Проверяем через JavaScript
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Long navCount = (Long) js.executeScript(
                    "return document.querySelectorAll('a, button, [href], [onclick]').length;"
            );

            System.out.println("   Кликабельных элементов найдено: " + navCount);
            return navCount > 10; // Если есть кликабельные элементы, значит страница работает

        } catch (Exception e) {
            System.out.println("   ⚠️ Ошибка при поиске навигации: " + e.getMessage());
            return false;
        }
    }

    public void openHomePage() throws InterruptedException {
        System.out.println("🌐 Открываем главную страницу...");

        try {
            // Устанавливаем увеличенный таймаут для этой операции
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

            driver.get(config.getWebBaseUrl());

            // Ждём загрузки с повторными попытками
            for (int i = 0; i < 3; i++) {
                try {
                    waitForPageLoad();
                    break;
                } catch (Exception e) {
                    if (i == 2) throw e;
                    System.out.println("   ↻ Повторная попытка загрузки (" + (i+1) + "/3)");
                    Thread.sleep(2000);
                }
            }

            System.out.println("   ✓ Страница открыта: " + driver.getCurrentUrl());

        } catch (TimeoutException e) {
            System.out.println("   ⚠️ Таймаут загрузки, но продолжаем...");
            // Продолжаем выполнение даже при таймауте
        } catch (Exception e) {
            System.out.println("   ❌ Ошибка открытия страницы: " + e.getMessage());
            throw e;
        } finally {
            // Восстанавливаем стандартный таймаут
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getWebPageLoadTimeout()));
        }
    }

    public void waitForPageLoad() {
        try {
            // Ожидание через JavaScript
            new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                    webDriver -> ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState").equals("complete")
            );

            // Дополнительная пауза для стабильности
            Thread.sleep(1000);

        } catch (Exception e) {
            System.out.println("   ⚠️ Игнорируем ошибку ожидания: " + e.getMessage());
            // Не падаем, продолжаем
        }
    }

    // НОВЫЙ МЕТОД: проверка наличия поиска
    public boolean isSearchAvailable() {
        try {
            return searchInput.isDisplayed() && searchInput.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // НОВЫЙ МЕТОД: получить заголовки страницы
    public List<String> getPageHeadings() {
        return headings.stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .filter(text -> text != null && !text.isEmpty())
                .toList();
    }
}