# OpenWeatherMap + Wikipedia Automation Project

## Описание проекта
Автоматизированные тесты для:
- **Веб-сайта OpenWeatherMap** (6 тестов)
- **Мобильного приложения Wikipedia** (4 теста)

Проект создан в рамках учебного задания по автоматизированному тестированию веб- и мобильных приложений с использованием Selenium WebDriver и Appium.

## Технологии
- **Язык:** Java 11+
- **Сборка:** Maven
- **Веб-тестирование:**
    - Selenium WebDriver 4.23.0
    - TestNG 7.10.2
    - WebDriverManager 5.9.2
- **Мобильное тестирование:**
    - Appium Java Client 9.2.3
    - UiAutomator2 (Android)
- **Логирование:** SLF4J 2.0.13


## Требования к окружению
### Общие требования:
- JDK 11 или выше
- Maven 3.6+
- Git

### Для веб-тестов:
- Браузер Chrome/Firefox/Edge (автоматически загружается через WebDriverManager)

### Для мобильных тестов:
- Node.js (для запуска Appium Server)
- Appium Server 2.0+
- Android Studio с установленным эмулятором Android
- Android SDK (API level 30+)
- APK файл Wikipedia (уже включен в проект)

## 🛠️ Установка и настройка

### 1. Клонирование репозитория

git clone <URL-репозитория>
cd openweather-appium-project

### 2. Настройка Android эмулятора

- Откройте Android Studio 
- Создайте или используйте существующий эмулятор Android с API 30+ 
- Запустите эмулятор командой
-     emulator -avd Medium_Phone_API_36.1 -no-snapshot &

<img width="450" height="1792" alt="image" src="https://github.com/user-attachments/assets/35193f53-d750-47f9-8891-f02a556ef0f1" />

### Убедитесь, что эмулятор виден через ADB:
  - adb devices

### 3. Запуск Appium Server

# Установите Appium если не установлен
    npm install -g appium

# Запустите Appium сервер
    appium --port 4723

## Запуск тестов

### Запуск всех тестов
    mvn clean test

<img width="642" height="560" alt="image" src="https://github.com/user-attachments/assets/69db72d6-0b20-48d4-a228-90fee66ddda7" />


### Запуск только веб-тестов
    mvn test -Dtest=OpenWeatherWebTests

<img width="750" height="492" alt="image" src="https://github.com/user-attachments/assets/24a0b312-5c4d-4591-9b00-25d6d3578314" />


### Запуск только мобильных тестов

    mvn test -Dtest=WikipediaMobileTest

<img width="1888" height="562" alt="image" src="https://github.com/user-attachments/assets/2e80f6b4-2bf8-42b2-b638-6cba1917dfc5" />
