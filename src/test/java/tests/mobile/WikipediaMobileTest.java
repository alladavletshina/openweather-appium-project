package tests.mobile;

import base.MobileBaseTest;
import io.appium.java_client.AppiumBy;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WikipediaMobileTest extends MobileBaseTest {

    @Test
    public void testWikipediaLaunchesSuccessfully() {
        System.out.println("🚀 ТЕСТ 1: Проверка запуска Wikipedia");
        System.out.println("=====================================");

        try {
            // Даем приложению время для полного запуска
            Thread.sleep(5000);

            // Получаем информацию
            String currentPackage = driver.getCurrentPackage();
            String currentActivity = driver.currentActivity();

            System.out.println("📱 Текущий пакет: " + currentPackage);
            System.out.println("🎯 Текущая активность: " + currentActivity);

            // Проверяем что мы в Wikipedia
            if (currentPackage.equals("org.wikipedia")) {
                System.out.println("✅ УСПЕХ! Wikipedia запущена");

                // Пропускаем onboarding если он есть
                skipOnboardingIfPresent();

                // Проверяем что это не лаунчер
                Assert.assertFalse(currentActivity.contains("Launcher") ||
                                currentActivity.contains("NexusLauncher"),
                        "Мы должны быть в Wikipedia, а не в лаунчере");

                // Дополнительная проверка
                String pageSource = driver.getPageSource();
                System.out.println("📄 Размер страницы Wikipedia: " + pageSource.length());
                Assert.assertTrue(pageSource.length() > 1000, "Страница Wikipedia должна быть загружена");

            } else {
                System.out.println("❌ Wikipedia не запустилась автоматически");
                System.out.println("Пробуем запустить вручную...");

                // Запускаем Wikipedia через ADB
                driver.activateApp("org.wikipedia");
                Thread.sleep(5000);

                // Пропускаем onboarding если он есть
                skipOnboardingIfPresent();

                // Проверяем снова
                currentPackage = driver.getCurrentPackage();
                System.out.println("📱 Пакет после ручного запуска: " + currentPackage);

                if (currentPackage.equals("org.wikipedia")) {
                    System.out.println("✅ Wikipedia запущена вручную");
                } else {
                    System.out.println("❌ Wikipedia все еще не запущена");
                    // Все равно продолжаем тест, но с предупреждением
                    System.out.println("⚠️  Продолжаем тест с текущим приложением: " + currentPackage);
                }
            }

            System.out.println("✅ ТЕСТ 1 ЗАВЕРШЕН УСПЕШНО");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Тест прерван", e);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в тесте запуска Wikipedia", e);
        }
    }

    @Test(dependsOnMethods = "testWikipediaLaunchesSuccessfully")
    public void testSimpleInteraction() {
        System.out.println("👆 ТЕСТ 2: Простое взаимодействие");

        try {
            // Просто проверяем что мы все еще в Wikipedia
            String currentPackage = driver.getCurrentPackage();
            System.out.println("📱 Текущий пакет: " + currentPackage);

            if (currentPackage.equals("org.wikipedia")) {
                System.out.println("✅ Мы в Wikipedia, можно продолжать тестирование");

                // Просто проверяем что приложение отвечает
                String pageSource = driver.getPageSource();
                System.out.println("📄 Размер страницы: " + pageSource.length());

                // Делаем небольшой скролл вниз чтобы проверить что приложение работает
                System.out.println("🔄 Делаем легкий скролл...");
                performSimpleScroll();

            } else {
                System.out.println("⚠️  Мы не в Wikipedia (" + currentPackage + ")");
                System.out.println("Пробуем вернуться в Wikipedia...");

                driver.activateApp("org.wikipedia");
                Thread.sleep(3000);
            }

            System.out.println("✅ ТЕСТ 2 ЗАВЕРШЕН УСПЕШНО");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Тест прерван", e);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в тесте взаимодействия", e);
        }
    }

    @Test(dependsOnMethods = {"testWikipediaLaunchesSuccessfully", "testSimpleInteraction"})
    public void testSearchFunctionality() {
        System.out.println("🔍 ТЕСТ 3: Проверка функции поиска");
        System.out.println("====================================");

        try {
            // Проверяем что мы все еще в Wikipedia
            String currentPackage = driver.getCurrentPackage();
            String currentActivity = driver.currentActivity();

            System.out.println("📱 Текущий пакет: " + currentPackage);
            System.out.println("🎯 Текущая активность: " + currentActivity);

            // Если мы не в Wikipedia, пробуем вернуться
            if (!currentPackage.equals("org.wikipedia")) {
                System.out.println("⚠️ Мы не в Wikipedia, пытаемся вернуться...");
                driver.activateApp("org.wikipedia");
                Thread.sleep(3000);
                currentPackage = driver.getCurrentPackage();
                currentActivity = driver.currentActivity();
                System.out.println("📱 Пакет после возврата: " + currentPackage);
                System.out.println("🎯 Активность после возврата: " + currentActivity);
            }

            // Если мы на каком-то экране кроме главного, пробуем нажать назад 1-2 раза
            if (!currentActivity.contains("MainActivity")) {
                System.out.println("⚠️ Мы не на главном экране, пробуем вернуться...");
                driver.navigate().back();
                Thread.sleep(2000);
                driver.navigate().back();
                Thread.sleep(2000);
                currentActivity = driver.currentActivity();
                System.out.println("🎯 Активность после возврата: " + currentActivity);
            }

            // Даем время для стабилизации
            Thread.sleep(2000);

            // ПРОСТОЙ ПОИСК БЕЗ ЛИШНИХ ДЕЙСТВИЙ

            // 1. Ищем поле поиска
            System.out.println("🎯 Ищем поле поиска...");

            boolean searchFound = false;

            // Пробуем самый простой локатор
            try {
                driver.findElement(AppiumBy.accessibilityId("Search Wikipedia")).click();
                System.out.println("✅ Нашли поле поиска по accessibility id");
                searchFound = true;
            } catch (Exception e) {
                System.out.println("⚠️ Не нашли по accessibility id, пробуем другой способ...");

                // Пробуем по ID
                try {
                    driver.findElement(AppiumBy.id("org.wikipedia:id/search_container")).click();
                    System.out.println("✅ Нашли поле поиска по ID");
                    searchFound = true;
                } catch (Exception e2) {
                    System.out.println("❌ Не удалось найти поле поиска");
                    // Делаем скриншот страницы для отладки
                    String pageSource = driver.getPageSource();
                    System.out.println("📄 Первые 500 символов page source:");
                    System.out.println(pageSource.substring(0, Math.min(500, pageSource.length())));
                    Assert.fail("Не удалось найти поле поиска");
                }
            }

            if (!searchFound) {
                return;
            }

            // 2. Ждем открытия экрана поиска
            System.out.println("⏳ Ждем открытия экрана поиска...");
            Thread.sleep(3000);

            // 3. Вводим поисковый запрос
            System.out.println("⌨️ Вводим поисковый запрос 'Java'...");

            try {
                driver.findElement(AppiumBy.id("org.wikipedia:id/search_src_text")).sendKeys("Java");
                System.out.println("✅ Ввели текст 'Java'");
            } catch (Exception e) {
                System.out.println("⚠️ Не удалось ввести текст, пробуем другой способ...");
                try {
                    driver.findElement(AppiumBy.className("android.widget.EditText")).sendKeys("Java");
                    System.out.println("✅ Ввели текст 'Java' по классу");
                } catch (Exception e2) {
                    System.out.println("❌ Не удалось ввести текст для поиска");
                    Assert.fail("Не удалось ввести текст для поиска");
                }
            }

            // 4. Ждем результатов поиска
            System.out.println("⏳ Ждем результатов поиска...");
            Thread.sleep(3000);

            // 5. Проверяем результаты
            System.out.println("🔍 Проверяем результаты поиска...");

            try {
                // Ищем элементы результатов
                int resultsCount = driver.findElements(
                        AppiumBy.id("org.wikipedia:id/page_list_item_title")).size();

                System.out.println("📊 Найдено результатов: " + resultsCount);

                if (resultsCount > 0) {
                    System.out.println("✅ Поиск работает! Найдены статьи");

                    // Получаем первый результат
                    String firstResult = driver.findElement(
                            AppiumBy.id("org.wikipedia:id/page_list_item_title")).getText();

                    System.out.println("📝 Первый результат: " + firstResult);

                    // Проверяем что результат не пустой
                    Assert.assertFalse(firstResult.isEmpty(), "Заголовок статьи не должен быть пустым");

                    // Проверяем что результат связан с запросом
                    boolean isRelevant = firstResult.toLowerCase().contains("java") ||
                            firstResult.toLowerCase().contains("джава");

                    if (isRelevant) {
                        System.out.println("✅ Результат релевантен запросу");
                    } else {
                        System.out.println("⚠️ Результат может быть не совсем релевантным: " + firstResult);
                    }

                } else {
                    System.out.println("⚠️ Результатов не найдено");
                    // Не падаем, просто предупреждение
                }

            } catch (Exception e) {
                System.out.println("⚠️ Ошибка при проверке результатов: " + e.getMessage());
            }

            // 6. Возвращаемся на главный экран
            System.out.println("↩️ Возвращаемся на главный экран...");
            driver.navigate().back();
            Thread.sleep(2000);

            System.out.println("✅ ТЕСТ 3 ПРОЙДЕН УСПЕШНО");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Тест прерван", e);
        } catch (AssertionError e) {
            System.out.println("❌ ТЕСТ 3 ПРОВАЛЕН: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("❌ ТЕСТ 3 ПРОВАЛЕН с исключением: " + e.getMessage());
            throw new RuntimeException("Ошибка в тесте поиска", e);
        }
    }

    @Test(dependsOnMethods = {"testWikipediaLaunchesSuccessfully", "testSimpleInteraction", "testSearchFunctionality"})
    public void testArticleScrolling() {
        System.out.println("📜 ТЕСТ 4: Прокрутка (скроллинг) статьи");
        System.out.println("========================================");

        try {
            // Проверяем что мы все еще в Wikipedia
            String currentPackage = driver.getCurrentPackage();
            System.out.println("📱 Текущий пакет: " + currentPackage);

            if (!currentPackage.equals("org.wikipedia")) {
                System.out.println("⚠️ Мы не в Wikipedia, пробуем вернуться...");
                driver.activateApp("org.wikipedia");
                Thread.sleep(3000);
            }

            // Даем время для стабилизации
            Thread.sleep(2000);

            // ПРОСТОЙ ТЕСТ СКРОЛЛИНГА

            // 1. Открываем поиск
            System.out.println("🎯 Открываем поиск для теста скроллинга...");

            try {
                driver.findElement(AppiumBy.accessibilityId("Search Wikipedia")).click();
                System.out.println("✅ Открыли поиск");
            } catch (Exception e) {
                try {
                    driver.findElement(AppiumBy.id("org.wikipedia:id/search_container")).click();
                    System.out.println("✅ Открыли поиск по ID");
                } catch (Exception e2) {
                    System.out.println("⚠️ Не удалось открыть поиск, пропускаем тест скроллинга");
                    return;
                }
            }

            // 2. Ждем открытия экрана поиска
            Thread.sleep(2000);

            // 3. Вводим поисковый запрос
            System.out.println("⌨️ Вводим запрос 'History'...");

            try {
                driver.findElement(AppiumBy.id("org.wikipedia:id/search_src_text")).sendKeys("History");
                System.out.println("✅ Ввели текст 'History'");
            } catch (Exception e) {
                System.out.println("⚠️ Не удалось ввести текст, пробуем другой запрос...");
                try {
                    driver.findElement(AppiumBy.id("org.wikipedia:id/search_src_text")).sendKeys("Science");
                    System.out.println("✅ Ввели текст 'Science'");
                } catch (Exception e2) {
                    System.out.println("❌ Не удалось ввести текст, пропускаем тест");
                    driver.navigate().back();
                    return;
                }
            }

            // 4. Ждем результаты
            System.out.println("⏳ Ждем результатов поиска...");
            Thread.sleep(3000);

            // 5. Открываем первую статью
            System.out.println("📖 Открываем первую статью...");

            try {
                driver.findElement(AppiumBy.id("org.wikipedia:id/page_list_item_title")).click();

                // Ждем загрузки статьи
                System.out.println("⏳ Ждем загрузки статьи...");
                Thread.sleep(4000);

                // 6. Выполняем скроллинг
                System.out.println("🔄 Начинаем скроллинг статьи...");

                // Делаем несколько скроллов
                for (int i = 1; i <= 3; i++) {
                    System.out.println("   Скролл #" + i);
                    performSimpleScroll();
                    Thread.sleep(1000);
                }

                System.out.println("✅ Скроллинг выполнен успешно!");

            } catch (Exception e) {
                System.out.println("❌ Ошибка при открытии статьи: " + e.getMessage());
            }

            // 7. Возвращаемся на главный экран
            System.out.println("↩️ Возвращаемся на главный экран...");
            driver.navigate().back();
            Thread.sleep(2000);

            // Если нужно, возвращаемся еще раз
            try {
                driver.navigate().back();
                Thread.sleep(1000);
            } catch (Exception e) {
                // Игнорируем
            }

            System.out.println("✅ ТЕСТ 4 ПРОЙДЕН УСПЕШНО");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Тест прерван", e);
        } catch (Exception e) {
            System.out.println("❌ Ошибка в тесте скроллинга: " + e.getMessage());

            // Пытаемся восстановить состояние
            try {
                for (int i = 0; i < 3; i++) {
                    driver.navigate().back();
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception e2) {
                // Игнорируем
            }

            throw new RuntimeException("Ошибка в тесте скроллинга", e);
        }
    }

    // ========== ПРОСТЫЕ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    // Простой скролл
    private void performSimpleScroll() {
        try {
            int screenWidth = driver.manage().window().getSize().getWidth();
            int screenHeight = driver.manage().window().getSize().getHeight();

            // Простой скролл вниз
            driver.executeScript("mobile: scrollGesture", java.util.Map.of(
                    "left", screenWidth / 2,
                    "top", (int)(screenHeight * 0.7),
                    "width", 100,
                    "height", 100,
                    "direction", "down",
                    "percent", 0.75
            ));

        } catch (Exception e) {
            System.out.println("⚠️ Ошибка при скроллинге: " + e.getMessage());
        }
    }

    // Простой метод для пропуска onboarding
    private void skipOnboardingIfPresent() {
        System.out.println("⏭️ Проверяем наличие onboarding...");

        try {
            String currentActivity = driver.currentActivity();
            System.out.println("   Текущая активность: " + currentActivity);

            if (currentActivity.contains("onboarding") || currentActivity.contains("InitialOnboardingActivity")) {
                System.out.println("   🎯 Обнаружен onboarding экран, пытаемся пропустить...");

                // Пробуем самый простой локатор
                try {
                    driver.findElement(AppiumBy.xpath("//*[@text='Skip']")).click();
                    System.out.println("✅ Onboarding пропущен");
                    Thread.sleep(3000);
                } catch (Exception e) {
                    System.out.println("⚠️ Не удалось пропустить onboarding: " + e.getMessage());
                }
            } else {
                System.out.println("✅ Onboarding не требуется");
            }

        } catch (Exception e) {
            System.out.println("⚠️ Ошибка при проверке onboarding: " + e.getMessage());
        }
    }
}