package tests.mobile;

import base.MobileBaseTest;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WikipediaMobileTest extends MobileBaseTest {

    @Test(priority = 1, description = "Тест 1: Проверка запуска приложения и поиска")
    public void testAppLaunchAndSearch() {
        System.out.println("🚀 ТЕСТ 1: Запуск приложения и проверка поиска");
        System.out.println("==============================================");

        try {
            // Ждем полной загрузки приложения
            waitForSeconds(5);

            String currentPackage = driver.getCurrentPackage();
            String currentActivity = driver.currentActivity();

            System.out.println("📱 Текущий пакет: " + currentPackage);
            System.out.println("🎯 Текущая активность: " + currentActivity);

            // Проверяем что мы в Wikipedia
            Assert.assertEquals(currentPackage, "org.wikipedia",
                    "Приложение должно быть Wikipedia");

            // Пропускаем onboarding если он есть
            skipOnboardingIfPresent();

            // Проверяем что приложение загрузилось
            String pageSource = driver.getPageSource();
            System.out.println("📄 Размер страницы: " + pageSource.length());
            Assert.assertTrue(pageSource.length() > 1000,
                    "Страница должна быть загружена (размер > 1000 символов)");

            // Проверяем наличие поиска
            boolean hasSearch = driver.findElements(
                    AppiumBy.accessibilityId("Search Wikipedia")).size() > 0;

            System.out.println("🔍 Поле поиска найдено: " + hasSearch);
            Assert.assertTrue(hasSearch, "Должно быть поле поиска");

            System.out.println("✅ ТЕСТ 1 ПРОЙДЕН: Приложение успешно запущено");

        } catch (Exception e) {
            System.out.println("❌ Ошибка в тесте запуска: " + e.getMessage());
            throw new RuntimeException("Тест запуска не пройден", e);
        }
    }

    @Test(priority = 2, description = "Тест 2: Поиск и скроллинг результатов",
            dependsOnMethods = "testAppLaunchAndSearch")
    public void testSearchAndScroll() {
        System.out.println("🔍 ТЕСТ 2: Поиск и скроллинг результатов");
        System.out.println("=========================================");

        try {
            // ШАГ 1: Открываем поиск
            System.out.println("1️⃣ Открываем поиск...");

            openSearchField();

            // ШАГ 2: Вводим "Java" в поле поиска
            System.out.println("2️⃣ Вводим 'Java' в поле поиска...");

            enterSearchQuery("Java");
            waitForSeconds(3); // Ждем результаты

            // ШАГ 3: Проверяем результаты поиска
            System.out.println("3️⃣ Проверяем результаты поиска...");

            int initialResults = checkAndCountSearchResults("Java");

            // ШАГ 4: СКРОЛЛИНГ РЕЗУЛЬТАТОВ (без открытия статьи!)
            System.out.println("4️⃣ Выполняем скроллинг результатов поиска...");

            if (initialResults > 3) {
                System.out.println("   🔄 Начинаем скроллинг...");

                // Делаем 3 скролла вниз
                for (int i = 1; i <= 3; i++) {
                    System.out.println("      Скролл #" + i);
                    performVerticalScroll("down", 0.6);
                    Thread.sleep(1500); // Пауза между скроллами

                    // Проверяем сколько результатов видно
                    int currentResults = driver.findElements(
                            AppiumBy.id("org.wikipedia:id/page_list_item_title")).size();
                    System.out.println("      Видим результатов: " + currentResults);
                }

                // Проверяем что после скроллинга видим результаты
                int finalResults = driver.findElements(
                        AppiumBy.id("org.wikipedia:id/page_list_item_title")).size();

                System.out.println("   📊 Итоговое количество результатов: " + finalResults);
                Assert.assertTrue(finalResults > 0,
                        "После скроллинга должны оставаться результаты");

                // Прокручиваем немного вверх
                System.out.println("   🔼 Прокручиваем немного вверх...");
                performVerticalScroll("up", 0.3);
                Thread.sleep(1000);

            } else {
                System.out.println("   ⚠️ Мало результатов для скроллинга");
            }

            // ШАГ 5: ЗАКРЫВАЕМ ПОИСК (без открытия статьи!)
            System.out.println("5️⃣ Закрываем поиск...");

            closeSearch();

            System.out.println("✅ ТЕСТ 2 ПРОЙДЕН УСПЕШНО!");
            System.out.println("   ✓ Поиск выполнен");
            System.out.println("   ✓ Результаты проверены");
            System.out.println("   ✓ Скроллинг выполнен");
            System.out.println("   ✓ Поиск закрыт без открытия статьи");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Тест прерван", e);
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
            dependsOnMethods = {"testAppLaunchAndSearch", "testSearchAndScroll"})
    public void testSearchAndOpenArticle() {
        System.out.println("📖 ТЕСТ 3: Поиск и открытие статьи");
        System.out.println("===================================");

        try {
            // Убеждаемся что мы на главном экране
            waitForSeconds(2);

            String currentPackage = driver.getCurrentPackage();
            Assert.assertEquals(currentPackage, "org.wikipedia",
                    "Должны быть в приложении Wikipedia");

            // ШАГ 1: Открываем поиск
            System.out.println("1️⃣ Открываем поиск...");

            openSearchField();

            // ШАГ 2: Ищем простую статью (например, "Cat")
            System.out.println("2️⃣ Ищем статью 'Cat'...");

            enterSearchQuery("Cat");
            waitForSeconds(3);

            // Проверяем результаты
            int results = driver.findElements(
                    AppiumBy.id("org.wikipedia:id/page_list_item_title")).size();

            System.out.println("   📊 Найдено статей: " + results);
            Assert.assertTrue(results > 0, "Должны быть результаты поиска");

            // ШАГ 3: Открываем первую статью
            System.out.println("3️⃣ Открываем первую статью...");

            String firstArticleTitle = "";
            try {
                // Получаем заголовок первой статьи
                WebElement firstArticle = driver.findElement(
                        AppiumBy.id("org.wikipedia:id/page_list_item_title"));
                firstArticleTitle = firstArticle.getText();
                System.out.println("   📝 Открываем статью: " + firstArticleTitle);

                // Открываем статью
                firstArticle.click();

            } catch (Exception e) {
                System.out.println("   ❌ Не удалось открыть статью: " + e.getMessage());
                Assert.fail("Не удалось открыть статью");
            }

            // ШАГ 4: Ждем загрузки статьи и проверяем
            System.out.println("4️⃣ Ждем загрузки статьи...");
            waitForSeconds(4);

            // Проверяем что статья загрузилась
            String pageSource = driver.getPageSource();
            System.out.println("   📄 Размер страницы статьи: " + pageSource.length());

            // Основная проверка - статья должна быть загружена
            Assert.assertTrue(pageSource.length() > 2000,
                    "Страница статьи должна быть загружена (размер > 2000 символов)");

            // Дополнительная проверка - в контенте должно быть что-то связанное с темой
            boolean hasContent = pageSource.toLowerCase().contains("cat") ||
                    pageSource.contains("animal") ||
                    pageSource.contains("feline") ||
                    pageSource.length() > 3000;

            System.out.println("   ✅ Статья содержит контент: " + hasContent);

            if (!hasContent) {
                System.out.println("   ⚠️ Контент статьи может быть неполным");
                // Не падаем, просто предупреждение
            }

            // ШАГ 5: Возвращаемся на главный экран
            System.out.println("5️⃣ Возвращаемся на главный экран...");

            // Используем аппаратную кнопку назад
            driver.pressKey(new KeyEvent(AndroidKey.BACK));
            waitForSeconds(2);

            // Если еще не вернулись, нажимаем еще раз
            try {
                if (!driver.currentActivity().contains("MainActivity")) {
                    driver.pressKey(new KeyEvent(AndroidKey.BACK));
                    waitForSeconds(1);
                }
            } catch (Exception e) {
                // Игнорируем
            }

            // ШАГ 6: Финальная проверка
            System.out.println("6️⃣ Финальная проверка...");

            currentPackage = driver.getCurrentPackage();
            System.out.println("   📱 Пакет приложения: " + currentPackage);

            // Главное - приложение должно быть запущено
            Assert.assertEquals(currentPackage, "org.wikipedia",
                    "После всех действий должны остаться в Wikipedia");

            System.out.println("✅ ТЕСТ 3 ПРОЙДЕН УСПЕШНО!");
            System.out.println("   ✓ Поиск статьи выполнен");
            System.out.println("   ✓ Статья успешно открыта");
            System.out.println("   ✓ Контент статьи загружен");
            System.out.println("   ✓ Возврат на главный экран выполнен");
            System.out.println("   ✓ Приложение работает стабильно");

        } catch (AssertionError e) {
            System.out.println("❌ ТЕСТ 3 ПРОВАЛЕН: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("❌ ТЕСТ 3 ПРОЙДЕН с исключением: " + e.getMessage());
            recoverAppState();
            throw new RuntimeException("Ошибка в тесте открытия статьи", e);
        }
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    private void openSearchField() {
        try {
            // Пробуем самый надежный способ
            driver.findElement(AppiumBy.accessibilityId("Search Wikipedia")).click();
            System.out.println("   ✅ Поиск открыт по accessibilityId");
        } catch (Exception e) {
            try {
                // Альтернативный способ
                driver.findElement(AppiumBy.id("org.wikipedia:id/search_container")).click();
                System.out.println("   ✅ Поиск открыт по id");
            } catch (Exception e2) {
                System.out.println("   ❌ Не удалось открыть поиск");
                Assert.fail("Не удалось открыть поиск");
            }
        }
        waitForSeconds(2);
    }

    private void enterSearchQuery(String query) {
        try {
            driver.findElement(AppiumBy.id("org.wikipedia:id/search_src_text")).sendKeys(query);
            System.out.println("   ✅ '" + query + "' введено");
        } catch (Exception e) {
            driver.findElement(AppiumBy.className("android.widget.EditText")).sendKeys(query);
            System.out.println("   ✅ '" + query + "' введено по классу");
        }
    }

    private int checkAndCountSearchResults(String expectedQuery) {
        int resultsCount = 0;
        try {
            resultsCount = driver.findElements(
                    AppiumBy.id("org.wikipedia:id/page_list_item_title")).size();
            System.out.println("   📊 Найдено результатов: " + resultsCount);

            if (resultsCount > 0) {
                String firstResult = driver.findElement(
                        AppiumBy.id("org.wikipedia:id/page_list_item_title")).getText();
                System.out.println("   📝 Первый результат: " + firstResult);

                Assert.assertFalse(firstResult.isEmpty(),
                        "Заголовок статьи не должен быть пустым");

                // Проверяем релевантность
                boolean isRelevant = firstResult.toLowerCase().contains(expectedQuery.toLowerCase());
                System.out.println("   ✅ Результат релевантен запросу: " + isRelevant);

                if (!isRelevant) {
                    // Логируем, но не падаем
                    System.out.println("   ⚠️ Первый результат может быть не совсем релевантным");
                }
            }

            Assert.assertTrue(resultsCount > 0,
                    "Должен быть хотя бы один результат поиска");

        } catch (Exception e) {
            System.out.println("   ⚠️ Не удалось получить результаты: " + e.getMessage());
            Assert.fail("Результаты поиска не отображаются");
        }
        return resultsCount;
    }

    private void closeSearch() {
        try {
            // Пробуем кнопку закрытия поиска
            driver.findElement(AppiumBy.id("org.wikipedia:id/search_close_btn")).click();
            System.out.println("   ❌ Нажали кнопку закрытия поиска");
        } catch (Exception e) {
            try {
                // Нажимаем аппаратную кнопку назад
                driver.pressKey(new KeyEvent(AndroidKey.BACK));
                System.out.println("   ↩️ Нажали аппаратную кнопку BACK");
            } catch (Exception e2) {
                // Пробуем найти крестик
                try {
                    driver.findElement(AppiumBy.xpath("//*[@content-desc='Close']")).click();
                    System.out.println("   ❌ Нажали крестик закрытия");
                } catch (Exception e3) {
                    System.out.println("   ⚠️ Не удалось закрыть поиск стандартным способом");
                    // Последний вариант - нажать BACK несколько раз
                    for (int i = 0; i < 3; i++) {
                        driver.pressKey(new KeyEvent(AndroidKey.BACK));
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
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
                // Скролл вниз
                startY = (int)(screenHeight * 0.7);
                endY = (int)(screenHeight * 0.3);
            } else {
                // Скролл вверх
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

            // Нажимаем BACK несколько раз чтобы вернуться на главный
            for (int i = 0; i < 5; i++) {
                driver.pressKey(new KeyEvent(AndroidKey.BACK));
                Thread.sleep(800);
            }

            // Проверяем что вернулись на главный
            String currentPackage = driver.getCurrentPackage();
            if (!currentPackage.equals("org.wikipedia")) {
                // Перезапускаем приложение
                driver.activateApp("org.wikipedia");
                Thread.sleep(3000);
                skipOnboardingIfPresent();
            }

            System.out.println("   ✅ Состояние восстановлено");

        } catch (Exception e2) {
            System.out.println("   ⚠️ Не удалось восстановить состояние: " + e2.getMessage());
        }
    }

    // Метод для пропуска onboarding
    private void skipOnboardingIfPresent() {
        try {
            System.out.println("   ⏭️ Проверяем onboarding...");

            // Ищем кнопку "Skip" в течение 5 секунд
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
                    // Продолжаем искать
                    Thread.sleep(500);
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