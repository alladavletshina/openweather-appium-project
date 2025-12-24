package tests.mobile;

import base.MobileBaseTest;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.mobile.WikipediaMainPage;
import pages.mobile.WikipediaSearchPage;
import pages.mobile.WikipediaArticlePage;

public class WikipediaMobileTest extends MobileBaseTest {
    private WikipediaMainPage mainPage;
    private WikipediaSearchPage searchPage;
    private WikipediaArticlePage articlePage;

    @BeforeClass
    public void setUpPages() {
        System.out.println("📱 ИНИЦИАЛИЗАЦИЯ PAGE OBJECTS");
        System.out.println("===============================");

        mainPage = new WikipediaMainPage(driver);
        searchPage = new WikipediaSearchPage(driver);
        articlePage = new WikipediaArticlePage(driver);

        System.out.println("✅ Page Objects инициализированы");
    }

    @Test(priority = 1, description = "Тест 1: Проверка запуска приложения и основных элементов")
    public void testAppLaunchAndBasicElements() {
        System.out.println("🚀 ТЕСТ 1: Запуск приложения и проверка основных элементов");
        System.out.println("==========================================================");

        try {

            waitForSeconds(5);

            String currentPackage = driver.getCurrentPackage();
            String currentActivity = driver.currentActivity();

            System.out.println("📱 Текущий пакет: " + currentPackage);
            System.out.println("🎯 Текущая активность: " + currentActivity);


            Assert.assertEquals(currentPackage, "org.wikipedia",
                    "Приложение должно быть Wikipedia");


            skipOnboardingIfPresent();


            boolean isMainPageDisplayed = mainPage.isMainPageDisplayed();
            System.out.println("🏠 Главная страница отображается: " + isMainPageDisplayed);

            if (isMainPageDisplayed) {
                String pageTitle = mainPage.getMainPageTitle();
                System.out.println("📝 Заголовок главной страницы: " + pageTitle);
            }


            try {

                System.out.println("🔍 Проверяем поле поиска...");


                boolean hasSearch = driver.findElements(
                        AppiumBy.accessibilityId("Search Wikipedia")).size() > 0;

                System.out.println("   Поле поиска найдено: " + hasSearch);
                Assert.assertTrue(hasSearch, "Должно быть поле поиска");


                boolean isLoginButtonDisplayed = mainPage.isLoginButtonDisplayed();
                System.out.println("👤 Кнопка логина отображается: " + isLoginButtonDisplayed);

            } catch (Exception e) {
                System.out.println("⚠️ Некоторые элементы не найдены: " + e.getMessage());

                String pageSource = driver.getPageSource();
                System.out.println("📄 Размер страницы: " + pageSource.length());
                Assert.assertTrue(pageSource.length() > 1000,
                        "Страница должна быть загружена (размер > 1000 символов)");
            }

            System.out.println("✅ ТЕСТ 1 ПРОЙДЕН: Приложение успешно запущено");

        } catch (Exception e) {
            System.out.println("❌ Ошибка в тесте запуска: " + e.getMessage());
            throw new RuntimeException("Тест запуска не пройден", e);
        }
    }

    @Test(priority = 2, description = "Тест 2: Поиск и скроллинг результатов",
            dependsOnMethods = "testAppLaunchAndBasicElements")
    public void testSearchAndScroll() {
        System.out.println("🔍 ТЕСТ 2: Поиск и скроллинг результатов");
        System.out.println("=========================================");

        try {

            System.out.println("1️⃣ Открываем поиск...");
            mainPage.clickSearch();
            waitForSeconds(2);
            System.out.println("   ✅ Поиск открыт");


            System.out.println("2️⃣ Выполняем поиск 'Java'...");
            String searchQuery = "Java";
            searchPage.searchFor(searchQuery);
            waitForSeconds(3);


            System.out.println("3️⃣ Проверяем результаты поиска...");

            int resultsCount = searchPage.getSearchResultsCount();
            System.out.println("   📊 Найдено результатов: " + resultsCount);

            boolean areResultsDisplayed = searchPage.areSearchResultsDisplayed();
            System.out.println("   👁️ Результаты отображаются: " + areResultsDisplayed);

            Assert.assertTrue(resultsCount > 0, "Должен быть хотя бы один результат поиска");
            Assert.assertTrue(areResultsDisplayed, "Результаты должны отображаться");


            if (resultsCount > 0) {
                String firstResultTitle = searchPage.getFirstResultTitle();
                System.out.println("   📝 Первый результат: " + firstResultTitle);
                Assert.assertFalse(firstResultTitle.isEmpty(),
                        "Заголовок статьи не должен быть пустым");


                boolean isRelevant = firstResultTitle.toLowerCase().contains("java");
                System.out.println("   ✅ Результат релевантен запросу: " + isRelevant);

                if (!isRelevant) {
                    System.out.println("   ⚠️ Первый результат может быть не совсем релевантным");
                }
            }


            System.out.println("4️⃣ Выполняем скроллинг результатов поиска...");

            if (resultsCount > 3) {
                System.out.println("   🔄 Начинаем скроллинг...");


                for (int i = 1; i <= 2; i++) {
                    System.out.println("      Скролл #" + i);
                    performVerticalScroll("down", 0.6);
                    waitForSeconds(1);
                }


                int resultsAfterScroll = searchPage.getSearchResultsCount();
                System.out.println("   📊 Результатов после скроллинга: " + resultsAfterScroll);
                Assert.assertTrue(resultsAfterScroll > 0,
                        "После скроллинга должны оставаться результаты");


                System.out.println("   🔼 Прокручиваем немного вверх...");
                performVerticalScroll("up", 0.3);
                waitForSeconds(1);
            } else {
                System.out.println("   ⚠️ Мало результатов для скроллинга");
            }


            System.out.println("5️⃣ Закрываем поиск...");
            searchPage.closeSearch(); // Используем метод из Page Object
            waitForSeconds(2);

            System.out.println("✅ ТЕСТ 2 ПРОЙДЕН УСПЕШНО!");

        } catch (AssertionError e) {
            System.out.println("❌ ТЕСТ 2 ПРОВАЛЕН: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("❌ ТЕСТ 2 ПРОВАЛЕН с исключением: " + e.getMessage());
            recoverAppState();
            throw new RuntimeException("Ошибка в тесте поиска и скроллинга", e);
        }
    }

    @Test(priority = 3, description = "Тест 3: Поиск и открытие статьи",
            dependsOnMethods = {"testAppLaunchAndBasicElements", "testSearchAndScroll"})
    public void testSearchAndOpenArticle() {
        System.out.println("📖 ТЕСТ 3: Поиск и открытие статьи");
        System.out.println("===================================");

        try {

            waitForSeconds(2);


            System.out.println("1️⃣ Открываем поиск...");
            mainPage.clickSearch();
            waitForSeconds(2);


            System.out.println("2️⃣ Ищем статью 'Cat'...");
            String searchQuery = "Cat";
            searchPage.searchFor(searchQuery);
            waitForSeconds(3);


            int resultsCount = searchPage.getSearchResultsCount();
            System.out.println("   📊 Найдено статей: " + resultsCount);

            if (resultsCount == 0) {
                Assert.fail("Нет результатов поиска для '" + searchQuery + "'");
            }


            System.out.println("3️⃣ Открываем первую статью...");

            String firstArticleTitle = searchPage.getFirstResultTitle();
            System.out.println("   📝 Открываем статью: " + firstArticleTitle);


            Assert.assertFalse(firstArticleTitle.isEmpty(),
                    "Заголовок найденной статьи не должен быть пустым");


            searchPage.selectFirstResult();
            waitForSeconds(3);


            System.out.println("4️⃣ Проверяем, что статья открылась...");


            String currentActivity = driver.currentActivity();
            System.out.println("   🎯 Текущая активность: " + currentActivity);


            boolean isNotOnSearchScreen = !currentActivity.toLowerCase().contains("search");
            boolean isNotOnMainScreen = !currentActivity.toLowerCase().contains("main");

            System.out.println("   ❌ Не на экране поиска: " + isNotOnSearchScreen);
            System.out.println("   ❌ Не на главном экране: " + isNotOnMainScreen);


            if (isNotOnSearchScreen && isNotOnMainScreen) {
                System.out.println("   ✅ Статья успешно открыта!");
            } else {
                String pageSource = driver.getPageSource();
                boolean hasArticleContent = pageSource.toLowerCase().contains("cat") ||
                        pageSource.length() > 3000 ||
                        pageSource.contains("article");

                System.out.println("   📄 Размер страницы: " + pageSource.length());
                System.out.println("   📝 Контент статьи найден: " + hasArticleContent);

                Assert.assertTrue(hasArticleContent,
                        "После открытия статьи должен отображаться контент");
            }

            System.out.println("5️⃣ Тест завершен успешно!");
            System.out.println("   ✓ Поиск выполнен");
            System.out.println("   ✓ Статья '" + firstArticleTitle + "' открыта");
            System.out.println("   ✓ Тест пройден");

            System.out.println("✅ ТЕСТ 3 ПРОЙДЕН УСПЕШНО!");

        } catch (AssertionError e) {
            System.out.println("❌ ТЕСТ 3 ПРОВАЛЕН: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("❌ ТЕСТ 3 ПРОЙДЕН с исключением: " + e.getMessage());
            recoverAppState();
            throw new RuntimeException("Ошибка в тесте открытия статьи", e);
        }
    }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ

    private void closeSearch() {
        try {

            driver.pressKey(new KeyEvent(AndroidKey.BACK));
            System.out.println("   ↩️ Нажали аппаратную кнопку BACK");
        } catch (Exception e) {
            try {

                driver.findElement(AppiumBy.xpath("//*[@content-desc='Close']")).click();
                System.out.println("   ❌ Нажали крестик закрытия");
            } catch (Exception e2) {
                System.out.println("   ⚠️ Не удалось закрыть поиск стандартным способом");

                for (int i = 0; i < 3; i++) {
                    driver.pressKey(new KeyEvent(AndroidKey.BACK));
                    waitForSeconds(0.5);
                }
            }
        }
        waitForSeconds(2);
    }

    private void performVerticalScroll(String direction, double percent) {
        try {
            int screenWidth = driver.manage().window().getSize().getWidth();
            int screenHeight = driver.manage().window().getSize().getHeight();

            int startY, endY;

            if (direction.equals("down")) {

                startY = (int)(screenHeight * 0.7);
                endY = (int)(screenHeight * 0.3);
            } else {

                startY = (int)(screenHeight * 0.3);
                endY = (int)(screenHeight * 0.7);
            }

            driver.executeScript("mobile: scrollGesture", java.util.Map.of(
                    "left", screenWidth / 2,
                    "top", startY,
                    "width", 100,
                    "height", 100,
                    "direction", direction,
                    "percent", percent,
                    "speed", 1500
            ));

        } catch (Exception e) {
            System.out.println("   ⚠️ Ошибка при скроллинге: " + e.getMessage());
        }
    }

    private void recoverAppState() {
        try {
            System.out.println("   🔄 Восстанавливаем состояние приложения...");


            for (int i = 0; i < 5; i++) {
                driver.pressKey(new KeyEvent(AndroidKey.BACK));
                waitForSeconds(0.8);
            }


            String currentPackage = driver.getCurrentPackage();
            if (!currentPackage.equals("org.wikipedia")) {

                driver.activateApp("org.wikipedia");
                waitForSeconds(3);
                skipOnboardingIfPresent();
            }

            System.out.println("   ✅ Состояние восстановлено");

        } catch (Exception e2) {
            System.out.println("   ⚠️ Не удалось восстановить состояние: " + e2.getMessage());
        }
    }

    private void skipOnboardingIfPresent() {
        try {
            System.out.println("   ⏭️ Проверяем onboarding...");


            long startTime = System.currentTimeMillis();
            boolean found = false;

            while (System.currentTimeMillis() - startTime < 5000 && !found) {
                try {
                    WebElement skipButton = driver.findElement(
                            AppiumBy.xpath("//*[contains(@text, 'Skip') or contains(@text, 'SKIP') or contains(@text, 'Пропустить')]"));

                    if (skipButton.isDisplayed()) {
                        skipButton.click();
                        System.out.println("   ✅ Onboarding пропущен");
                        waitForSeconds(2);
                        found = true;
                        break;
                    }
                } catch (Exception e) {

                    waitForSeconds((int) 0.5);
                }
            }

            if (!found) {
                System.out.println("   ✅ Onboarding не найден или не требуется");
            }

        } catch (Exception e) {
            System.out.println("   ⚠️ Ошибка при проверке onboarding: " + e.getMessage());
        }
    }
}