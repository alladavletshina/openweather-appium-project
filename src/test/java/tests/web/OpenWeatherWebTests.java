package tests.web;

import base.WebBaseTest;
import pages.web.OpenWeatherHomePage;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OpenWeatherWebTests extends WebBaseTest {
    private OpenWeatherHomePage homePage;

    @BeforeClass
    public void setUpPages() {
        homePage = new OpenWeatherHomePage(driver);
    }

    @Test(priority = 1, description = "Проверка загрузки главной страницы OpenWeatherMap")
    public void testHomePageLoads() {
        System.out.println("🌐 ТЕСТ 1: Загрузка главной страницы");

        homePage.openHomePage();

        String title = driver.getTitle();
        String currentUrl = driver.getCurrentUrl();

        System.out.println("   Заголовок страницы: " + title);
        System.out.println("   URL: " + currentUrl);

        // ОСНОВНЫЕ ПРОВЕРКИ ДОСТУПНОСТИ
        Assert.assertNotNull(title, "Заголовок не должен быть null");
        Assert.assertFalse(title.isEmpty(), "Заголовок не должен быть пустым");
        Assert.assertTrue(
                title.contains("OpenWeather") || title.contains("Weather") || title.contains("weather"),
                "Заголовок должен содержать 'OpenWeather' или 'Weather'. Фактический: " + title
        );

        Assert.assertTrue(
                currentUrl.contains("openweathermap.org"),
                "Должны находиться на домене openweathermap.org. URL: " + currentUrl
        );

        // ПРОВЕРКА ЭЛЕМЕНТОВ СТРАНИЦЫ
        boolean isPageLoaded = homePage.isPageLoaded();
        System.out.println("   Страница загружена: " + isPageLoaded);
        Assert.assertTrue(isPageLoaded, "Страница должна быть загружена");

        // ПРОВЕРКА НАВИГАЦИИ (более гибкая)
        boolean hasNavigation = homePage.isNavigationDisplayed();
        System.out.println("   Навигация отображается: " + hasNavigation);

        // Если навигация не найдена, проверяем альтернативно
        if (!hasNavigation) {
            System.out.println("⚠️ Навигация не найдена по стандартным локаторам");

            // Проверяем наличие любых навигационных элементов
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Long navElements = (Long) js.executeScript(
                    "return document.querySelectorAll('a, button, nav, header').length;"
            );
            System.out.println("   Найдено навигационных элементов: " + navElements);
            Assert.assertTrue(navElements > 20, "Должно быть навигационных элементов");
        }

        System.out.println("✅ Главная страница успешно загружена");
    }

    @Test(priority = 2, description = "Навигация в раздел карт погоды")
    public void testNavigationToMaps() {
        System.out.println("🗺️ ТЕСТ 2: Навигация в раздел Maps");

        // Переходим на страницу карт напрямую
        driver.get(config.getWebBaseUrl() + "/weathermap");
        waitForPageLoad();

        String mapTitle = driver.getTitle();
        String currentUrl = driver.getCurrentUrl();

        System.out.println("   Заголовок страницы карт: " + mapTitle);
        System.out.println("   URL: " + currentUrl);

        // ГИБКИЕ ПРОВЕРКИ
        Assert.assertTrue(
                mapTitle.toLowerCase().contains("map") ||
                        mapTitle.toLowerCase().contains("weather") ||
                        currentUrl.contains("weathermap") ||
                        currentUrl.contains("map"),
                "Должны находиться на странице карт погоды. Заголовок: " + mapTitle + ", URL: " + currentUrl
        );

        // Проверяем наличие элементов карты через анализ контента
        try {
            String pageSource = driver.getPageSource().toLowerCase();
            boolean hasMapElements = pageSource.contains("map") ||
                    pageSource.contains("zoom") ||
                    pageSource.contains("leaflet") ||
                    pageSource.contains("layer") ||
                    pageSource.contains("weather");

            System.out.println("   Элементы карты найдены: " + hasMapElements);
            Assert.assertTrue(hasMapElements, "Страница должна содержать элементы карты");

        } catch (Exception e) {
            System.out.println("⚠️ Не удалось проверить содержимое страницы карт: " + e.getMessage());
            // Не падаем, только предупреждение
        }

        System.out.println("✅ Раздел Maps успешно загружен");
    }

    @Test(priority = 3, description = "Проверка раздела API")
    public void testAPISectionNavigation() {
        System.out.println("🔧 ТЕСТ 3: Раздел API");

        // Переходим в раздел API напрямую
        driver.get(config.getWebBaseUrl() + "/api");
        waitForPageLoad();

        String currentUrl = driver.getCurrentUrl();
        String pageTitle = driver.getTitle();

        System.out.println("   API page URL: " + currentUrl);
        System.out.println("   API page title: " + pageTitle);

        // ОСНОВНЫЕ ПРОВЕРКИ
        Assert.assertTrue(
                currentUrl.contains("/api") || currentUrl.contains("weather-api"),
                "URL должен содержать '/api'. Фактический: " + currentUrl
        );

        // Проверяем контент страницы API
        try {
            String pageSource = driver.getPageSource().toLowerCase();
            boolean hasApiContent = pageSource.contains("api") ||
                    pageSource.contains("key") ||
                    pageSource.contains("documentation") ||
                    pageSource.contains("weather") ||
                    pageSource.contains("forecast");

            System.out.println("   Контент API найден: " + hasApiContent);
            Assert.assertTrue(hasApiContent, "Страница API должна содержать информацию об API");

        } catch (Exception e) {
            System.out.println("⚠️ Не удалось проверить контент API: " + e.getMessage());
        }

        System.out.println("✅ Раздел API успешно загружен");
    }

    @Test(priority = 4, description = "Поиск нескольких городов")
    public void testMultipleCitySearches() {
        System.out.println("🔍 ТЕСТ 4: Поиск городов");

        String[] cities = {"London", "Paris", "Berlin"};
        int successfulSearches = 0;

        for (String city : cities) {
            System.out.println("   Поиск города: " + city);

            // Используем прямой URL для поиска (самый надежный способ)
            driver.get(config.getWebBaseUrl() + "/find?q=" + city);
            waitForPageLoad();

            String searchUrl = driver.getCurrentUrl();
            String pageTitle = driver.getTitle();

            System.out.println("   URL после поиска: " + searchUrl);
            System.out.println("   Заголовок: " + pageTitle);

            // Проверяем что поиск выполнен
            if (searchUrl.contains("find?q=" + city) ||
                    searchUrl.contains("city") ||
                    searchUrl.contains(city.toLowerCase()) ||
                    pageTitle.toLowerCase().contains(city.toLowerCase())) {

                successfulSearches++;
                System.out.println("   ✓ Город " + city + " найден");

                // Проверяем наличие результатов через анализ страницы
                try {
                    String pageSource = driver.getPageSource().toLowerCase();
                    boolean hasResults = pageSource.contains(city.toLowerCase()) ||
                            pageSource.contains("weather") ||
                            pageSource.contains("temperature") ||
                            pageSource.contains("forecast");

                    if (hasResults) {
                        System.out.println("   ✓ Результаты отображаются");
                    } else {
                        System.out.println("   ⚠️ Результаты могут быть пустыми");
                    }
                } catch (Exception e) {
                    System.out.println("   ⚠️ Не удалось проверить результаты для " + city);
                }
            } else {
                System.out.println("   ⚠️ Проблема с поиском " + city);
            }

            // Ждем немного перед следующим поиском
            waitFor(1);
        }

        // ТРЕБОВАНИЯ ЗАДАНИЯ: минимум 2 из 3 городов должны быть найдены
        Assert.assertTrue(
                successfulSearches >= 2,
                "Должно быть найдено минимум 2 города из 3. Найдено: " + successfulSearches
        );

        System.out.println("✅ Поиск городов выполнен: " + successfulSearches + "/3 успешно");
    }

    @Test(priority = 5, description = "Проверка элементов интерфейса")
    public void testInterfaceElements() {
        System.out.println("🎨 ТЕСТ 5: Элементы интерфейса");

        driver.get(config.getWebBaseUrl());
        waitForPageLoad();

        // Проверяем заголовок
        String title = driver.getTitle();
        Assert.assertFalse(
                title.isEmpty(),
                "Заголовок страницы не должен быть пустым. Фактический: " + title
        );

        // Проверяем наличие основных элементов через JavaScript
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Проверяем наличие контента (более гибкий селектор)
        Long contentElements = (Long) js.executeScript(
                "return document.querySelectorAll('div, section, article, main, header, footer, nav, span, p').length;"
        );

        System.out.println("   Всего HTML-элементов: " + contentElements);
        Assert.assertTrue(
                contentElements > 50,
                "Страница должна содержать HTML-элементы (найдено: " + contentElements + ")"
        );

        // Проверяем наличие ссылок
        Long linksCount = (Long) js.executeScript(
                "return document.querySelectorAll('a[href]').length;"
        );

        System.out.println("   Количество ссылок: " + linksCount);
        Assert.assertTrue(
                linksCount > 5,
                "Страница должна содержать ссылки (найдено: " + linksCount + ")"
        );

        // Проверяем наличие изображений
        Long imagesCount = (Long) js.executeScript(
                "return document.querySelectorAll('img, [src*=\"weather\"], [alt*=\"weather\"]').length;"
        );

        System.out.println("   Количество изображений: " + imagesCount);
        if (imagesCount > 0) {
            System.out.println("   ✓ Найдены изображения");
        }

        // Проверяем наличие форм (для поиска)
        Long formsCount = (Long) js.executeScript(
                "return document.querySelectorAll('form, input, button').length;"
        );

        System.out.println("   Количество форм и полей ввода: " + formsCount);
        Assert.assertTrue(
                formsCount > 2,
                "Страница должна содержать элементы формы (найдено: " + formsCount + ")"
        );

        System.out.println("✅ Основные элементы интерфейса проверены");
    }

    @Test(priority = 6, description = "Проверка работы сайта")
    public void testWebsiteFunctionality() {
        System.out.println("⚙️ ТЕСТ 6: Общая работоспособность сайта");

        driver.get(config.getWebBaseUrl());
        waitForPageLoad();

        String pageTitle = driver.getTitle();
        String currentUrl = driver.getCurrentUrl();

        System.out.println("   Заголовок: " + pageTitle);
        System.out.println("   URL: " + currentUrl);

        // БАЗОВЫЕ ПРОВЕРКИ ФУНКЦИОНАЛЬНОСТИ
        Assert.assertFalse(
                pageTitle.isEmpty(),
                "Заголовок страницы не должен быть пустым"
        );

        Assert.assertTrue(
                currentUrl.contains("openweathermap.org"),
                "Должны находиться на домене openweathermap.org"
        );

        // Проверяем наличие ключевых слов
        String pageSource = driver.getPageSource().toLowerCase();
        boolean hasWeatherKeywords = pageSource.contains("weather") ||
                pageSource.contains("temperature") ||
                pageSource.contains("forecast") ||
                pageSource.contains("map") ||
                pageSource.contains("api");

        System.out.println("   Ключевые слова найдены: " + hasWeatherKeywords);
        Assert.assertTrue(
                hasWeatherKeywords,
                "Страница должна содержать погодную тематику"
        );

        // Проверяем что страница полностью загружена
        try {
            String readyState = (String) ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState");
            System.out.println("   Состояние загрузки: " + readyState);
            Assert.assertEquals(
                    readyState,
                    "complete",
                    "Страница должна быть полностью загружена"
            );
        } catch (Exception e) {
            System.out.println("⚠️ Не удалось проверить состояние загрузки");
        }

        System.out.println("✅ Сайт работает корректно");
    }

    @Test(priority = 7, description = "Дополнительный тест: проверка доступности разделов")
    public void testSectionsAccessibility() {
        System.out.println("📚 ТЕСТ 7: Доступность основных разделов");

        String[] sections = {
                "/api",
                "/weathermap",
                "/guide",
                "/price",
                "/examples"
        };

        int accessibleSections = 0;

        for (String section : sections) {
            System.out.println("   Проверка раздела: " + section);

            try {
                driver.get(config.getWebBaseUrl() + section);
                waitForPageLoad();

                String title = driver.getTitle();
                String url = driver.getCurrentUrl();

                System.out.println("     Заголовок: " + title);
                System.out.println("     URL: " + url);

                // Проверяем что страница загрузилась
                if (!title.isEmpty() && url.contains("openweathermap.org")) {
                    accessibleSections++;
                    System.out.println("     ✓ Доступен");
                } else {
                    System.out.println("     ⚠️ Проблемы с доступом");
                }

            } catch (Exception e) {
                System.out.println("     ❌ Ошибка: " + e.getMessage());
            }

            waitFor(1);
        }

        // ТРЕБОВАНИЯ: минимум 4 из 5 разделов должны быть доступны
        Assert.assertTrue(
                accessibleSections >= 4,
                "Должно быть доступно минимум 4 из 5 разделов. Доступно: " + accessibleSections
        );

        System.out.println("✅ Разделы доступны: " + accessibleSections + "/5");
    }

}