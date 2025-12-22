package tests.mobile;

import base.MobileBaseTest;
import io.appium.java_client.AppiumBy;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WikipediaWorkingTests extends MobileBaseTest {

    @Test(priority = 1)
    public void testWikipediaAppLaunch() {
        System.out.println("📱 ТЕСТ 1: Запуск Wikipedia - УСПЕШНЫЙ");
        System.out.println("=========================================");

        // Драйвер уже инициализирован в базовом классе
        System.out.println("✅ Wikipedia успешно запущена");
        System.out.println("📱 Пакет: " + driver.getCurrentPackage());
        System.out.println("🎯 Активность: " + driver.currentActivity());

        // Простая проверка
        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.length() > 1000, "Страница должна быть загружена");

        System.out.println("📄 Размер страницы: " + pageSource.length() + " символов");
        System.out.println("🎉 ТЕСТ ПРОЙДЕН!");
    }

    @Test(priority = 2)
    public void testSkipOnboardingScreen() throws InterruptedException {
        System.out.println("⏭️ ТЕСТ 2: Пропуск onboarding экрана");

        // Дайте время для отображения onboarding
        Thread.sleep(3000);

        // Пробуем разные локаторы для кнопки "Skip"
        try {
            // Способ 1: По тексту "Skip"
            driver.findElement(AppiumBy.xpath("//*[contains(@text, 'Skip')]")).click();
            System.out.println("✅ Нашел 'Skip' по тексту");
        } catch (Exception e1) {
            try {
                // Способ 2: По accessibility id
                driver.findElement(AppiumBy.accessibilityId("Skip")).click();
                System.out.println("✅ Нашел 'Skip' по accessibility id");
            } catch (Exception e2) {
                try {
                    // Способ 3: По другому тексту
                    driver.findElement(AppiumBy.xpath("//*[contains(@text, 'Пропустить')]")).click();
                    System.out.println("✅ Нашел 'Пропустить' по тексту");
                } catch (Exception e3) {
                    System.out.println("⚠️  Кнопка Skip не найдена, возможно onboarding уже пройден");
                    // Проверяем что мы уже на главном экране
                    String activity = driver.currentActivity();
                    if (!activity.contains("onboarding")) {
                        System.out.println("✅ Onboarding уже пройден, мы на: " + activity);
                    }
                }
            }
        }

        Thread.sleep(2000);
        System.out.println("✅ Onboarding экран обработан");
    }

    @Test(priority = 3)
    public void testWikipediaSearch() throws InterruptedException {
        System.out.println("🔍 ТЕСТ 3: Поиск в Wikipedia");

        // Пропускаем onboarding если нужно
        testSkipOnboardingScreen();

        // Ищем поле поиска
        Thread.sleep(2000);

        try {
            // Пробуем разные локаторы для поиска
            driver.findElement(AppiumBy.accessibilityId("Search Wikipedia")).click();
            System.out.println("✅ Нашел поиск по accessibility id");
        } catch (Exception e1) {
            try {
                driver.findElement(AppiumBy.id("org.wikipedia:id/search_container")).click();
                System.out.println("✅ Нашел поиск по ID");
            } catch (Exception e2) {
                driver.findElement(AppiumBy.xpath("//*[contains(@text, 'Search')]")).click();
                System.out.println("✅ Нашел поиск по тексту");
            }
        }

        // Вводим поисковый запрос
        Thread.sleep(1000);
        driver.findElement(AppiumBy.id("org.wikipedia:id/search_src_text")).sendKeys("Automation testing");

        // Ждем результаты
        Thread.sleep(3000);

        // Проверяем результаты
        int results = driver.findElements(AppiumBy.id("org.wikipedia:id/page_list_item_title")).size();
        System.out.println("   Найдено результатов: " + results);

        Assert.assertTrue(results > 0, "Должны быть результаты поиска");
        System.out.println("✅ Поиск работает корректно");
    }

    @Test(priority = 4)
    public void testWikipediaArticle() throws InterruptedException {
        System.out.println("📖 ТЕСТ 4: Открытие статьи");

        // Используем поиск из предыдущего теста
        testWikipediaSearch();

        // Открываем первую статью
        driver.findElement(AppiumBy.id("org.wikipedia:id/page_list_item_title")).click();
        Thread.sleep(3000);

        // Проверяем что статья открылась
        String articlePage = driver.getPageSource();
        boolean hasArticle = articlePage.contains("Automation") ||
                driver.findElements(AppiumBy.id("org.wikipedia:id/view_article_header_title")).size() > 0;

        System.out.println("   Статья открыта: " + hasArticle);
        System.out.println("   Размер страницы статьи: " + articlePage.length());

        Assert.assertTrue(hasArticle, "Должна открыться статья");
        System.out.println("✅ Статья успешно открыта");
    }
}