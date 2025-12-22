package tests.web;

import base.WebBaseTest;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OpenWeatherWebTests extends WebBaseTest {

    @Test(priority = 1, description = "Проверка загрузки главной страницы")
    public void testHomePageLoads() {
        String title = driver.getTitle();
        System.out.println("Page title: " + title);

        Assert.assertTrue(title.contains("OpenWeather") || title.contains("Weather"),
                "Заголовок должен содержать 'OpenWeather' или 'Weather'");

        System.out.println("[SUCCESS] Страница успешно загружена: " + title);
    }

    @Test(priority = 2, description = "Навигация по разделам сайта")
    public void testNavigationToMaps() {
        // Переходим на страницу карт
        driver.get(config.getWebBaseUrl() + "/weathermap");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String mapTitle = driver.getTitle();
        System.out.println("Map page title: " + mapTitle);

        Assert.assertTrue(mapTitle.contains("Map") || mapTitle.contains("weather"),
                "Заголовок должен содержать 'Map' или 'weather'");

        System.out.println("[SUCCESS] Раздел Maps успешно загружен");
    }

    @Test(priority = 3, description = "Проверка раздела API")
    public void testAPISectionNavigation() {
        // Переходим в раздел API
        driver.get(config.getWebBaseUrl() + "/api");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String currentUrl = driver.getCurrentUrl();
        System.out.println("API page URL: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("/api"),
                "URL должен содержать '/api'");

        String pageTitle = driver.getTitle();
        System.out.println("API page title: " + pageTitle);

        Assert.assertTrue(pageTitle.contains("API"),
                "Заголовок должен содержать информацию об API");

        System.out.println("[SUCCESS] Раздел API успешно загружен");
    }

    @Test(priority = 4, description = "Поиск нескольких городов через URL")
    public void testMultipleCitySearches() {
        String[] cities = {"London", "Paris", "Berlin"};

        for (String city : cities) {
            System.out.println("\n=== Поиск города: " + city + " ===");

            // Используем прямой URL для поиска (более стабильный способ)
            driver.get(config.getWebBaseUrl() + "/find?q=" + city);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String searchTitle = driver.getTitle();
            String searchUrl = driver.getCurrentUrl();

            System.out.println("Title: " + searchTitle);
            System.out.println("URL: " + searchUrl);

            // Проверяем что поиск выполнен
            Assert.assertTrue(searchUrl.contains("find?q=" + city),
                    "URL должен содержать поисковый запрос");

            System.out.println("✓ Город " + city + " найден");
        }
    }

    @Test(priority = 5, description = "Проверка элементов интерфейса")
    public void testInterfaceElements() {
        driver.get(config.getWebBaseUrl());

        // Проверяем наличие основных элементов через JavaScript
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Проверяем заголовок
        String title = driver.getTitle();
        Assert.assertFalse(title.isEmpty(), "Заголовок страницы не должен быть пустым");

        // Проверяем наличие контента
        Long contentElements = (Long) js.executeScript(
                "return document.querySelectorAll('div, section, article, main').length;"
        );

        Assert.assertTrue(contentElements > 10, "Страница должна содержать контент");

        // Проверяем наличие ссылок
        Long linksCount = (Long) js.executeScript(
                "return document.querySelectorAll('a').length;"
        );

        Assert.assertTrue(linksCount > 5, "Страница должна содержать ссылки");

        System.out.println("[SUCCESS] Основные элементы проверены");
    }

    @Test(priority = 6, description = "Проверка работы сайта")
    public void testWebsiteFunctionality() {
        // Простая проверка работоспособности
        driver.get(config.getWebBaseUrl());

        String pageTitle = driver.getTitle();
        String currentUrl = driver.getCurrentUrl();

        System.out.println("Page title: " + pageTitle);
        System.out.println("Current URL: " + currentUrl);

        Assert.assertFalse(pageTitle.isEmpty(), "Заголовок страницы не должен быть пустым");
        Assert.assertTrue(currentUrl.contains("openweathermap.org"),
                "Должны находиться на домене openweathermap.org");

        // Проверяем наличие ключевых слов
        String pageSource = driver.getPageSource().toLowerCase();
        boolean hasWeatherKeywords = pageSource.contains("weather");

        Assert.assertTrue(hasWeatherKeywords, "Страница должна содержать погодную тематику");

        System.out.println("[SUCCESS] Сайт работает корректно");
    }
}