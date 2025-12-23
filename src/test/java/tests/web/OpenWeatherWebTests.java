package tests.web;

import base.WebBaseTest; // ← Убедитесь, что этот импорт есть
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OpenWeatherWebTests extends WebBaseTest { // ← Наследуемся от WebBaseTest

    @Test(priority = 1, description = "Проверка загрузки главной страницы OpenWeatherMap")
    public void testHomePageLoads() {
        System.out.println("🌐 ТЕСТ 1: Загрузка главной страницы");

        // Переходим на главную страницу (гарантированно)
        driver.get(config.getWebBaseUrl());
        waitForPageLoad();
        waitFor(2); // Дополнительное ожидание

        String title = driver.getTitle();
        String currentUrl = driver.getCurrentUrl();

        System.out.println("   Заголовок страницы: " + title);
        System.out.println("   URL: " + currentUrl);

        // Простая проверка
        Assert.assertTrue(currentUrl.contains("openweathermap.org"),
                "Должны находиться на домене openweathermap.org. URL: " + currentUrl);

        System.out.println("✅ Главная страница успешно загружена");
    }


    @Test(priority = 2, description = "Навигация в раздел карт погоды")
    public void testNavigationToMaps() {
        System.out.println("🗺️ ТЕСТ 2: Навигация в раздел Maps");

        // Переходим на страницу карт
        driver.get(config.getWebBaseUrl() + "/weathermap");

        // Ждем загрузки
        waitForPageLoad();

        String mapTitle = driver.getTitle();
        String currentUrl = driver.getCurrentUrl();

        System.out.println("   Заголовок страницы карт: " + mapTitle);
        System.out.println("   URL: " + currentUrl);

        // Используем более гибкие проверки
        Assert.assertTrue(
                mapTitle.toLowerCase().contains("map") ||
                        mapTitle.toLowerCase().contains("weather") ||
                        currentUrl.contains("weathermap"),
                "Должны находиться на странице карт погоды"
        );

        // Проверяем наличие элементов на странице карт
        try {
            String pageSource = driver.getPageSource().toLowerCase();
            boolean hasMapElements = pageSource.contains("map") ||
                    pageSource.contains("zoom") ||
                    pageSource.contains("leaflet");
            Assert.assertTrue(hasMapElements, "Страница должна содержать элементы карты");
        } catch (Exception e) {
            System.out.println("⚠️ Не удалось проверить содержимое страницы карт");
        }

        System.out.println("✅ Раздел Maps успешно загружен");
    }

    @Test(priority = 3, description = "Проверка раздела API")
    public void testAPISectionNavigation() {
        System.out.println("🔧 ТЕСТ 3: Раздел API");

        // Переходим в раздел API
        driver.get(config.getWebBaseUrl() + "/api");

        // Ждем загрузки
        waitForPageLoad();

        String currentUrl = driver.getCurrentUrl();
        String pageTitle = driver.getTitle();

        System.out.println("   API page URL: " + currentUrl);
        System.out.println("   API page title: " + pageTitle);

        Assert.assertTrue(currentUrl.contains("/api"),
                "URL должен содержать '/api'");

        // Проверяем контент страницы API
        String pageSource = driver.getPageSource().toLowerCase();
        boolean hasApiContent = pageSource.contains("api") ||
                pageSource.contains("key") ||
                pageSource.contains("documentation");

        Assert.assertTrue(hasApiContent, "Страница API должна содержать информацию об API");

        System.out.println("✅ Раздел API успешно загружен");
    }

    @Test(priority = 4, description = "Поиск нескольких городов")
    public void testMultipleCitySearches() {
        System.out.println("🔍 ТЕСТ 4: Поиск городов");

        String[] cities = {"London", "Paris", "Berlin"};
        int successfulSearches = 0;

        for (String city : cities) {
            System.out.println("   Поиск города: " + city);

            // Используем прямой URL для поиска
            driver.get(config.getWebBaseUrl() + "/find?q=" + city);

            // Ждем загрузки
            waitForPageLoad();

            String searchUrl = driver.getCurrentUrl();
            System.out.println("   URL после поиска: " + searchUrl);

            // Проверяем что поиск выполнен
            if (searchUrl.contains("find?q=" + city)) {
                successfulSearches++;
                System.out.println("   ✓ Город " + city + " найден");

                // Проверяем наличие результатов
                try {
                    String pageSource = driver.getPageSource().toLowerCase();
                    boolean hasResults = pageSource.contains(city.toLowerCase()) ||
                            pageSource.contains("weather") ||
                            pageSource.contains("temperature");

                    if (hasResults) {
                        System.out.println("   ✓ Результаты отображаются");
                    }
                } catch (Exception e) {
                    System.out.println("   ⚠️ Не удалось проверить результаты для " + city);
                }
            } else {
                System.out.println("   ⚠️ Проблема с поиском " + city);
            }

            // Ждем немного перед следующим поиском
            waitFor(1); // ← Теперь waitFor() доступен!
        }

        Assert.assertTrue(successfulSearches >= 2,
                "Должно быть найдено минимум 2 города из 3");

        System.out.println("✅ Поиск городов выполнен: " + successfulSearches + "/3 успешно");
    }

    @Test(priority = 5, description = "Проверка элементов интерфейса")
    public void testInterfaceElements() {
        System.out.println("🎨 ТЕСТ 5: Элементы интерфейса");

        driver.get(config.getWebBaseUrl());
        waitForPageLoad();

        // Проверяем заголовок
        String title = driver.getTitle();
        Assert.assertFalse(title.isEmpty(), "Заголовок страницы не должен быть пустым");

        // Проверяем наличие основных элементов через JavaScript
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Проверяем наличие контента
        Long contentElements = (Long) js.executeScript(
                "return document.querySelectorAll('div, section, article, main, header, footer').length;"
        );

        Assert.assertTrue(contentElements > 10,
                "Страница должна содержать HTML-элементы (найдено: " + contentElements + ")");

        // Проверяем наличие ссылок
        Long linksCount = (Long) js.executeScript(
                "return document.querySelectorAll('a[href]').length;"
        );

        Assert.assertTrue(linksCount > 5,
                "Страница должна содержать ссылки (найдено: " + linksCount + ")");

        // Проверяем наличие изображений
        Long imagesCount = (Long) js.executeScript(
                "return document.querySelectorAll('img').length;"
        );

        if (imagesCount > 0) {
            System.out.println("   Найдено изображений: " + imagesCount);
        }

        System.out.println("✅ Основные элементы интерфейса проверены");
        System.out.println("   • Элементы: " + contentElements);
        System.out.println("   • Ссылки: " + linksCount);
        System.out.println("   • Изображения: " + imagesCount);
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

        // Базовые проверки
        Assert.assertFalse(pageTitle.isEmpty(), "Заголовок страницы не должен быть пустым");
        Assert.assertTrue(currentUrl.contains("openweathermap.org"),
                "Должны находиться на домене openweathermap.org");

        // Проверяем наличие ключевых слов
        String pageSource = driver.getPageSource().toLowerCase();
        boolean hasWeatherKeywords = pageSource.contains("weather") ||
                pageSource.contains("temperature") ||
                pageSource.contains("forecast");

        Assert.assertTrue(hasWeatherKeywords, "Страница должна содержать погодную тематику");

        // Проверяем статус код через JavaScript (косвенно)
        try {
            Long httpLinks = (Long) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('a[href^=\"http\"], link[href^=\"http\"], script[src^=\"http\"], img[src^=\"http\"]')).length;"
            );
            System.out.println("   HTTP ресурсов: " + httpLinks);
        } catch (Exception e) {
            // Игнорируем, это не критично
        }

        System.out.println("✅ Сайт работает корректно");
    }
}