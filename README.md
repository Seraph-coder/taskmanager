# Task Manager — Telegram Bot

Современное приложение для управления личными задачами.
Проект написан на Java 21/Spring Boot.

---

## Описание

Проект реализует функционал управления задачами через Telegram-бота: добавление, просмотр, отметка выполненных и
удаление задач.
Данные сохраняются в PostgreSQL с использованием JPA/Hibernate.

---

## Команды бота

| Команда     | Описание                                              |
|-------------|-------------------------------------------------------|
| `/start`    | Приветствие и справка по командам                     |
| `/help`     | Справка по командам                                   |
| `/add`      | Добавить задачу (интерактивный режим)                 |
| `/todo`     | Показать список невыполненных задач                   |
| `/done`     | Отметить задачу как выполненную (интерактивный режим) |
| `/showdone` | Показать список выполненных задач                     |
| `/delete`   | Удалить задачу (интерактивный режим)                  |
| `/cancel`   | Отменить текущее действие                             |

Бот поддерживает:

- **Inline-кнопки** для удобного взаимодействия
- **Машину состояний** для диалогов с пользователем
- **Обработку ошибок** с понятными сообщениями
- **Rate limiting** для защиты от спама

---

## Быстрый старт

### Требования

* Java 21+
* Maven 3.6+
* PostgreSQL 12+

### Шаги

1. Склонируйте репозиторий:

   ```bash
   git clone https://github.com/yourname/taskmanager.git
   cd taskmanager
   ```

2. Создайте файл `env.properties` в корне проекта и добавьте токен бота:

   ```properties
   TELEGRAM_BOT_TOKEN=ваш_токен_от_BotFather
   ```

   Получить токен можно у [@BotFather](https://t.me/BotFather) в Telegram.

3. Настройте параметры подключения к PostgreSQL.

   В файле `src/main/resources/application.properties` укажите параметры:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
   spring.datasource.username=ваш_пользователь
   spring.datasource.password=ваш_пароль
   spring.jpa.hibernate.ddl-auto=update
   ```

4. Соберите проект:

   ```bash
   mvn clean package
   ```

5. Запустите приложение:

   ```bash
   java -jar target/taskmanager-0.0.1-SNAPSHOT.jar
   ```

---

## Примечания по тестам

* В тестах используется настройка `telegrambots.enabled=false` (см. `src/test/resources/application.properties`),
  чтобы избежать реальных подключений к Telegram при запуске тестов.
* Интеграционные тесты используют встроенную базу H2, которая создаётся и удаляется автоматически.
* Покрытие тестами включает:
    - Интеграционные тесты сервисов
    - Тесты машины состояний
    - Тесты обработчиков команд и callback
    - Юнит-тесты для rate limiting

Запуск всех тестов:

```bash
mvn test
```

---

## Структура проекта

```
src/main/java/ru/naujava/taskmanager/
├── entity/                 # JPA-сущности (Task, User, UserState, TelegramIdState)
├── repository/             # Spring Data JPA репозитории
├── service/                # Бизнес-логика
├── state/                  # Машина состояний и обработчики
│   ├── StateMachine.java                  # Центральная машина состояний
│   ├── StateHandler.java                  # Интерфейс обработчика состояний
│   ├── StateTransition.java               # Модель перехода состояния
│   ├── AwaitingTaskDescriptionHandler.java
│   └── AwaitingTaskIdForDeletionHandler.java
├── controller/             # Обработка команд и callback
│   ├── command/            # Команды бота (Strategy Pattern)
│   │   ├── BotCommand.java              # Интерфейс команды
│   │   ├── StartCommand.java
│   │   ├── HelpCommand.java
│   │   ├── AddTaskCommand.java
│   │   ├── TodoListCommand.java
│   │   ├── DeleteTaskCommand.java
│   │   └── CancelCommand.java
│   ├── callback/           # Callback-стратегии (Strategy Pattern)
│   │   ├── CallbackStrategy.java        # Интерфейс стратегии
│   │   ├── AddTaskCallbackStrategy.java
│   │   ├── DeleteTaskCallbackStrategy.java
│   │   ├── ListTasksCallbackStrategy.java
│   │   └── CancelCallbackStrategy.java
│   ├── CommandHandler.java              # Маршрутизатор команд
│   ├── CallbackHandler.java             # Маршрутизатор callback
│   └── CommandResponse.java             # Модель ответа
├── keyboard/               # Модель клавиатуры (Domain Layer)
│   ├── model/
│   │   ├── Keyboard.java               # Модель клавиатуры
│   │   ├── KeyboardButton.java         # Модель кнопки
│   │   ├── KeyboardRow.java            # Модель ряда кнопок
│   │   └── KeyboardType.java           # Типы клавиатур (enum)
│   └── KeyboardProvider.java           # Провайдер клавиатур
├── bot/                    # Интеграция с Telegram API (Infrastructure Layer)
│   ├── keyboard/
│   │   ├── KeyboardFactory.java         # Преобразование в Telegram API
│   │   └── KeyboardService.java         # Создание конкретных клавиатур
│   ├── TelegramBot.java                 # Основной класс бота
│   ├── BotMessageProcessor.java         # Процессор сообщений
│   ├── BotResponse.java                 # Модель ответа бота
│   ├── MessageRateLimiter.java          # Rate limiting
│   └── BotConstants.java                # Константы (сообщения, callback)
└── config/                 # Конфигурация Spring
    └── BotConfig.java

src/test/java/ru/naujava/taskmanager/
├── service/                # Интеграционные тесты сервисов
│   ├── TaskServiceIntegrationTest.java
│   ├── UserServiceIntegrationTest.java
│   └── TelegramIdStateServiceIntegrationTest.java
├── state/                  # Тесты машины состояний
│   └── StateMachineIntegrationTest.java
├── controller/             # Тесты обработчиков
│   └── CallbackHandlerIntegrationTest.java
└── bot/                    # Тесты бота
    ├── BotMessageProcessorTest.java
    └── MessageRateLimiterTest.java
```

---

## Технологический стек

- **Java 21** — современная версия с pattern matching и record
- **Spring Boot 3.5.7** — фреймворк для создания приложений
- **Spring Data JPA** — работа с базой данных
- **Hibernate** — ORM для маппинга объектов
- **PostgreSQL** — реляционная база данных
- **H2** — in-memory БД для тестов
- **TelegramBots API 7.11.0** — интеграция с Telegram
- **JUnit 5** — тестирование
- **Mockito** — мокирование для тестов
- **Maven** — сборка проекта

---

## Где смотреть код

### Основные компоненты:

* **Сущности**: `src/main/java/ru/naujava/taskmanager/entity`
* **Сервисы**: `src/main/java/ru/naujava/taskmanager/service`
* **Репозитории**: `src/main/java/ru/naujava/taskmanager/repository`

### Машина состояний:

* **StateMachine**: `src/main/java/ru/naujava/taskmanager/state/StateMachine.java`
* **Обработчики**: `src/main/java/ru/naujava/taskmanager/state/`

### Команды и callback:

* **Команды**: `src/main/java/ru/naujava/taskmanager/controller/command/`
* **Callback**: `src/main/java/ru/naujava/taskmanager/controller/callback/`
* **Обработчики**: `src/main/java/ru/naujava/taskmanager/controller/`

### Telegram интеграция:

* **Бот**: `src/main/java/ru/naujava/taskmanager/bot/TelegramBot.java`
* **Обработчик сообщений**: `src/main/java/ru/naujava/taskmanager/bot/BotMessageProcessor.java`
* **Клавиатуры**: `src/main/java/ru/naujava/taskmanager/bot/keyboard/`

### Модель клавиатуры:

* **Абстракция**: `src/main/java/ru/naujava/taskmanager/keyboard/`

### Конфигурация:

* **BotConfig**: `src/main/java/ru/naujava/taskmanager/config/BotConfig.java`

### Тесты:

* **Все тесты**: `src/test/java/ru/naujava/taskmanager/`

---

## Лицензия

Этот проект создан в образовательных целях.

---

## Автор

**Seraph-coder**