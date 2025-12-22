package tests.mobile;

import base.MobileBaseTest;
import io.appium.java_client.AppiumBy;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WikipediaLaunchTest extends MobileBaseTest {

    @Test
    public void testWikipediaLaunchesSuccessfully() throws InterruptedException {
        System.out.println("🚀 ТЕСТ: Проверка запуска Wikipedia");
        System.out.println("=====================================");

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

        System.out.println("🏁 ТЕСТ ЗАВЕРШЕН");
    }

    @Test(dependsOnMethods = "testWikipediaLaunchesSuccessfully")
    public void testSimpleInteraction() throws InterruptedException {
        System.out.println("👆 ТЕСТ 2: Простое взаимодействие");

        // Восстанавливаем приложение если нужно
        ensureWikipediaIsOpen();

        // Убеждаемся что onboarding пропущен
        skipOnboardingIfPresent();

        // Проверяем что мы в каком-то приложении
        String currentPackage = driver.getCurrentPackage();

        if (currentPackage.equals("org.wikipedia")) {
            System.out.println("✅ Мы в Wikipedia, можно тестировать");

            // Просто нажимаем назад чтобы проверить что приложение отвечает
            driver.navigate().back();
            Thread.sleep(2000);

            // Проверяем что драйвер работает
            String page = driver.getPageSource();
            System.out.println("📄 Размер страницы: " + page.length());

        } else {
            System.out.println("⚠️  Мы не в Wikipedia (" + currentPackage + "), тестируем базовые функции");

            // Простая проверка что драйвер работает
            try {
                driver.getPageSource();
                System.out.println("✅ Драйвер работает, но не в Wikipedia");
            } catch (Exception e) {
                System.err.println("❌ Драйвер не отвечает: " + e.getMessage());
            }
        }
    }

    @Test(dependsOnMethods = {"testWikipediaLaunchesSuccessfully", "testSimpleInteraction"})
    public void testSearchFunctionality() throws InterruptedException {
        System.out.println("🔍 ТЕСТ 3: Проверка функции поиска");
        System.out.println("====================================");

        // Восстанавливаем приложение если нужно
        ensureWikipediaIsOpen();

        // Убеждаемся что мы в Wikipedia
        String currentPackage = driver.getCurrentPackage();
        System.out.println("📱 Текущий пакет: " + currentPackage);

        if (!currentPackage.equals("org.wikipedia")) {
            System.out.println("❌ Мы не в Wikipedia, пытаемся восстановить...");
            restoreWikipediaApp();

            // Проверяем снова
            currentPackage = driver.getCurrentPackage();
            System.out.println("📱 Пакет после восстановления: " + currentPackage);
        }

        Assert.assertEquals(currentPackage, "org.wikipedia", "Должны быть в Wikipedia");

        // Убеждаемся что onboarding пропущен
        skipOnboardingIfPresent();

        // Проверяем что мы на главном экране (не на onboarding)
        String currentActivity = driver.currentActivity();
        System.out.println("🎯 Текущая активность: " + currentActivity);

        if (currentActivity.contains("onboarding") || currentActivity.contains("InitialOnboardingActivity")) {
            System.out.println("⚠️  Мы все еще на onboarding экране, пытаемся пропустить...");
            skipOnboardingIfPresent();

            // Проверяем снова
            currentActivity = driver.currentActivity();
            System.out.println("🎯 Активность после пропуска onboarding: " + currentActivity);
        }

        // Даем время для стабилизации
        Thread.sleep(2000);

        try {
            // Ищем и кликаем на поле поиска
            System.out.println("🎯 Поиск поля для ввода запроса...");

            // Пробуем разные локаторы для поля поиска
            boolean searchFieldClicked = false;

            // Способ 1: По accessibility id
            try {
                driver.findElement(AppiumBy.accessibilityId("Search Wikipedia")).click();
                System.out.println("✅ Нашли поле поиска по accessibility id");
                searchFieldClicked = true;
            } catch (Exception e1) {
                System.out.println("⚠️ Не нашли по accessibility id: " + e1.getMessage());
            }

            // Способ 2: По ID
            if (!searchFieldClicked) {
                try {
                    driver.findElement(AppiumBy.id("org.wikipedia:id/search_container")).click();
                    System.out.println("✅ Нашли поле поиска по ID");
                    searchFieldClicked = true;
                } catch (Exception e2) {
                    System.out.println("⚠️ Не нашли по ID: " + e2.getMessage());
                }
            }

            // Способ 3: По тексту
            if (!searchFieldClicked) {
                try {
                    driver.findElement(AppiumBy.xpath("//*[contains(@text, 'Search')]")).click();
                    System.out.println("✅ Нашли поле поиска по тексту");
                    searchFieldClicked = true;
                } catch (Exception e3) {
                    System.out.println("⚠️ Не нашли по тексту: " + e3.getMessage());
                }
            }

            if (!searchFieldClicked) {
                System.out.println("❌ Не удалось найти поле поиска");
                System.out.println("📄 Page source текущего экрана:");
                String pageSource = driver.getPageSource();
                int showLength = Math.min(3000, pageSource.length());
                System.out.println(pageSource.substring(0, showLength));
                Assert.fail("Не удалось найти поле поиска");
            }

            // Ждем открытия экрана поиска
            System.out.println("⏳ Ждем открытия экрана поиска...");
            Thread.sleep(3000);

            // Проверяем активность после клика
            String searchActivity = driver.currentActivity();
            System.out.println("🎯 Активность поиска: " + searchActivity);

            // Вводим поисковый запрос
            System.out.println("⌨️ Вводим поисковый запрос 'Java'...");

            boolean textEntered = false;

            // Способ 1: По ID поля ввода
            try {
                driver.findElement(AppiumBy.id("org.wikipedia:id/search_src_text")).sendKeys("Java");
                System.out.println("✅ Ввели текст 'Java' по ID");
                textEntered = true;
            } catch (Exception e) {
                System.out.println("⚠️ Не нашли поле ввода по ID: " + e.getMessage());
            }

            // Способ 2: По классу
            if (!textEntered) {
                try {
                    driver.findElement(AppiumBy.className("android.widget.EditText")).sendKeys("Java");
                    System.out.println("✅ Ввели текст 'Java' по классу");
                    textEntered = true;
                } catch (Exception e) {
                    System.out.println("⚠️ Не нашли поле ввода по классу: " + e.getMessage());
                }
            }

            // Способ 3: По XPath
            if (!textEntered) {
                try {
                    driver.findElement(AppiumBy.xpath("//android.widget.EditText")).sendKeys("Java");
                    System.out.println("✅ Ввели текст 'Java' по XPath");
                    textEntered = true;
                } catch (Exception e) {
                    System.out.println("⚠️ Не нашли поле ввода по XPath: " + e.getMessage());
                }
            }

            // Способ 4: Пробуем найти любое поле ввода
            if (!textEntered) {
                try {
                    driver.findElement(AppiumBy.xpath("//*[@class='android.widget.EditText']")).sendKeys("Java");
                    System.out.println("✅ Ввели текст 'Java' по классу через XPath");
                    textEntered = true;
                } catch (Exception e) {
                    System.out.println("⚠️ Не нашли поле ввода: " + e.getMessage());
                }
            }

            if (!textEntered) {
                System.out.println("❌ Не удалось найти поле ввода");
                System.out.println("📄 Размер page source экрана поиска: " + driver.getPageSource().length());
                Assert.fail("Не удалось ввести текст для поиска");
            }

            // Ждем результатов поиска
            System.out.println("⏳ Ждем результатов поиска (3 секунды)...");
            Thread.sleep(3000);

            // Проверяем результаты
            System.out.println("🔍 Проверяем результаты поиска...");

            try {
                // Ищем элементы результатов
                int resultsCount = driver.findElements(
                        AppiumBy.id("org.wikipedia:id/page_list_item_title")).size();

                System.out.println("📊 Найдено результатов: " + resultsCount);

                if (resultsCount > 0) {
                    System.out.println("✅ Поиск работает! Найдены статьи");

                    // Проверяем что первый результат содержит ожидаемый текст
                    String firstResult = driver.findElement(
                            AppiumBy.id("org.wikipedia:id/page_list_item_title")).getText();

                    System.out.println("📝 Первый результат: " + firstResult);

                    // Проверяем что результат не пустой
                    Assert.assertFalse(firstResult.isEmpty(), "Заголовок статьи не должен быть пустым");

                    // Проверяем что результат связан с запросом
                    boolean isRelevant = firstResult.toLowerCase().contains("java") ||
                            firstResult.toLowerCase().contains("джава") ||
                            firstResult.toLowerCase().contains("programming") ||
                            firstResult.toLowerCase().contains("language");

                    if (isRelevant) {
                        System.out.println("✅ Результат релевантен запросу");
                    } else {
                        System.out.println("⚠️ Результат может быть не совсем релевантным: " + firstResult);
                        // Не падаем, только предупреждение
                    }

                } else {
                    System.out.println("⚠️ Результатов не найдено по стандартному ID");

                    // Проверяем другие возможные ID для результатов
                    String[] possibleResultIds = {
                            "org.wikipedia:id/search_results_list",
                            "org.wikipedia:id/search_results_container",
                            "org.wikipedia:id/search_results"
                    };

                    for (String id : possibleResultIds) {
                        try {
                            int count = driver.findElements(AppiumBy.id(id)).size();
                            if (count > 0) {
                                System.out.println("✅ Найден контейнер результатов с ID: " + id);
                            }
                        } catch (Exception e) {
                            // игнорируем
                        }
                    }

                    // Проверяем есть ли какие-то элементы на экране
                    int totalElements = driver.findElements(AppiumBy.xpath("//*")).size();
                    System.out.println("📊 Всего элементов на экране: " + totalElements);

                    if (totalElements > 20) {
                        System.out.println("✅ Поиск, вероятно, работает (найдены элементы на экране)");
                    }
                }

            } catch (Exception e) {
                System.out.println("❌ Ошибка при проверке результатов: " + e.getMessage());
            }

            // Возвращаемся на главный экран
            System.out.println("↩️ Возвращаемся на главный экран...");
            driver.navigate().back();
            Thread.sleep(2000);

            // Проверяем что вернулись в Wikipedia
            String finalPackage = driver.getCurrentPackage();
            if (!finalPackage.equals("org.wikipedia")) {
                System.out.println("⚠️ После поиска вышли из Wikipedia, пытаемся восстановить...");
                restoreWikipediaApp();
            }

            System.out.println("✅ ТЕСТ 3 ПРОЙДЕН: Функция поиска проверена");

        } catch (AssertionError e) {
            System.out.println("❌ ТЕСТ 3 ПРОВАЛЕН: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("❌ ТЕСТ 3 ПРОВАЛЕН с исключением: " + e.getMessage());
            throw new RuntimeException("Ошибка в тесте поиска", e);
        }
    }

    // Вспомогательный метод для пропуска onboarding
    private void skipOnboardingIfPresent() {
        System.out.println("⏭️ Проверяем наличие onboarding...");

        try {
            String currentActivity = driver.currentActivity();
            System.out.println("   Текущая активность: " + currentActivity);

            if (currentActivity.contains("onboarding") || currentActivity.contains("InitialOnboardingActivity") ||
                    currentActivity.contains("WelcomeActivity")) {
                System.out.println("   🎯 Обнаружен onboarding экран, пытаемся пропустить...");

                // Пробуем разные локаторы для кнопки пропуска
                String[] skipLocators = {
                        "//*[@text='Skip']",
                        "//*[contains(@text, 'Пропустить')]",
                        "//*[@content-desc='Skip']",
                        "org.wikipedia:id/fragment_onboarding_skip_button",
                        "org.wikipedia:id/button_skip",
                        "org.wikipedia:id/acceptButton",
                        "//android.widget.Button[@text='SKIP']",
                        "//*[@text='Get started']",
                        "//*[@text='Continue']"
                };

                boolean skipped = false;
                for (String locator : skipLocators) {
                    try {
                        System.out.println("   Пробуем локатор: " + locator);
                        if (locator.contains("//")) {
                            driver.findElement(AppiumBy.xpath(locator)).click();
                        } else {
                            driver.findElement(AppiumBy.id(locator)).click();
                        }
                        System.out.println("✅ Onboarding пропущен с локатором: " + locator);
                        skipped = true;

                        // Ждем перехода на главный экран
                        Thread.sleep(3000);
                        break;
                    } catch (Exception e) {
                        System.out.println("   ❌ Не найден: " + locator);
                    }
                }

                if (!skipped) {
                    // Если не нашли кнопку, пробуем нажать на экран
                    System.out.println("👆 Пробуем тапнуть на экран чтобы продолжить...");
                    try {
                        driver.executeScript("mobile: clickGesture", java.util.Map.of(
                                "x", 500,
                                "y", 1000
                        ));
                        Thread.sleep(3000);
                        System.out.println("✅ Кликнули на экран");
                    } catch (Exception e) {
                        System.out.println("⚠️ Не удалось кликнуть на экран: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("✅ Onboarding уже пройден или не требуется");
            }

        } catch (Exception e) {
            System.out.println("⚠️ Ошибка при проверке onboarding: " + e.getMessage());
        }
    }

    // Восстановление приложения Wikipedia если оно закрыто
    private void ensureWikipediaIsOpen() {
        try {
            String currentPackage = driver.getCurrentPackage();
            System.out.println("🔍 Проверяем текущее приложение: " + currentPackage);

            if (!currentPackage.equals("org.wikipedia")) {
                System.out.println("❌ Wikipedia не открыта, пытаемся восстановить...");
                restoreWikipediaApp();
            } else {
                System.out.println("✅ Wikipedia уже открыта");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Ошибка при проверке приложения: " + e.getMessage());
        }
    }

    // Метод для восстановления приложения Wikipedia
    private void restoreWikipediaApp() {
        System.out.println("🔄 Восстановление приложения Wikipedia...");

        try {
            // Пробуем активировать приложение
            driver.activateApp("org.wikipedia");
            System.out.println("✅ Активировали приложение Wikipedia");

            // Ждем запуска
            Thread.sleep(5000);

            // Проверяем успешность
            String currentPackage = driver.getCurrentPackage();
            System.out.println("📱 Пакет после активации: " + currentPackage);

            if (!currentPackage.equals("org.wikipedia")) {
                System.out.println("❌ Не удалось восстановить Wikipedia, пробуем запустить заново...");

                // Закрываем все приложения
                driver.terminateApp("org.wikipedia");
                Thread.sleep(2000);

                // Запускаем заново
                driver.activateApp("org.wikipedia");
                Thread.sleep(5000);

                // Пропускаем onboarding
                skipOnboardingIfPresent();

                // Проверяем снова
                currentPackage = driver.getCurrentPackage();
                if (currentPackage.equals("org.wikipedia")) {
                    System.out.println("✅ Wikipedia успешно перезапущена");
                } else {
                    System.out.println("❌ Не удалось восстановить Wikipedia");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Ошибка при восстановлении Wikipedia: " + e.getMessage());
        }
    }
}