# Task Manager — Telegram Bot

Современное приложение — Telegram-бот для управления личными задачами.
Проект написан на Java 21/Spring Boot и демонстрирует многослойную архитектуру с применением принципов SOLID.

---

## Описание

Проект реализует функционал управления задачами через Telegram-бота: добавление, просмотр и удаление задач.
Данные сохраняются в PostgreSQL с использованием JPA/Hibernate.

Ключевые особенности:

- **Стратегия (Strategy Pattern)** — для обработки команд и callback-запросов
- **Машина состояний** — для управления диалогами с пользователем
- **Dependency Injection** — через Spring Framework
- **Разделение ответственности** — чистая архитектура с четким разделением слоев
- **Масштабируемость** — легко добавлять новые команды и состояния

---

## Архитектура

Проект следует принципам SOLID и использует паттерны проектирования для обеспечения расширяемости:

### Слои приложения:

* **entity** — JPA-сущности (`Task`, `User`, `UserState`, `TelegramIdState`)
* **repository** — интерфейсы JPA (`TaskRepository`, `UserRepository`, `TelegramIdStateRepository`)
* **service** — бизнес-логика (`TaskService`, `UserService`, `TelegramIdStateService`)
* **state** — машина состояний и обработчики:
    * `StateMachine` — маршрутизация сообщений к обработчикам на основе состояния
    * `MessageHandler` — интерфейс для обработки сообщений
    * `StateHandler` — интерфейс для обработчиков состояний
    * `StateTransition` — модель перехода состояния
    * Обработчики состояний: `AwaitingTaskDescriptionHandler`, `AwaitingTaskIdForDeletionHandler`
* **controller** — обработка команд и callback с использованием паттерна Strategy:
    * `command/` — реализации команд (`StartCommand`, `AddTaskCommand`, `TodoListCommand`, и т.д.)
    * `callback/` — стратегии для callback-запросов (`AddTaskCallbackStrategy`, `DeleteTaskCallbackStrategy`, и т.д.)
    * `CommandHandler` — маршрутизация команд к соответствующим стратегиям
    * `CallbackHandler` — маршрутизация callback к соответствующим стратегиям
    * `Action` — enum для действий после команд
    * `CommandResponse` — модель ответа команды
* **keyboard** — модель клавиатуры (абстракция от Telegram API):
    * `model/` — доменные модели (`Keyboard`, `KeyboardButton`, `KeyboardRow`, `KeyboardType`)
    * `KeyboardProvider` — провайдер клавиатур, предоставляет клавиатуру по типу
* **bot** — интеграция с Telegram API:
    * `keyboard/` — сервисы для работы с клавиатурами:
        * `KeyboardFactory` — преобразование доменных моделей в формат Telegram API
        * `KeyboardService` — создание конкретных клавиатур (главное меню, отмена)
    * `TelegramBot` — основной класс бота, обработка обновлений от Telegram
    * `BotMessageProcessor` — процессор сообщений с rate limiting
    * `MessageRateLimiter` — защита от спама
    * `BotResponse` — модель ответа бота
    * `BotConstants` — константы (сообщения, callback-данные, тексты кнопок)
* **config** — конфигурация Spring (`BotConfig`)

### Принципы проектирования:

1. **Open-Closed Principle (OCP)**:
    - Новые команды и callback-стратегии добавляются без изменения существующего кода
    - Использование Map вместо switch/case для маршрутизации
    - Автоматическая регистрация стратегий через Spring DI

2. **Single Responsibility Principle (SRP)**:
    - Каждый класс отвечает за одну конкретную задачу
    - Разделение на слои: presentation (bot), application (controller), domain (service), infrastructure (repository)

3. **Dependency Inversion Principle (DIP)**:
    - Зависимости направлены на абстракции (интерфейсы), а не на конкретные реализации
    - KeyboardProvider зависит от KeyboardService через интерфейс
    - StateMachine работает с MessageHandler, а не с конкретными обработчиками

4. **Strategy Pattern**:
    - Команды реализуют интерфейс `BotCommand`
    - Callback реализуют интерфейс `CallbackStrategy`
    - Обработчики состояний реализуют интерфейс `MessageHandler`

5. **State Pattern**:
    - Для управления диалогами и переходами между состояниями
    - Каждое состояние имеет свой обработчик
    - Переходы между состояниями управляются через `StateTransition`

6. **Abstraction Layer**:
    - Доменные модели клавиатур изолированы от Telegram API
    - Только `KeyboardFactory` и `TelegramBot` зависят от библиотеки Telegram
    - Легко заменить Telegram на другую платформу

### Преимущества текущей архитектуры:

* **Нет switch/case** — все маршрутизация через Map и стратегии
* **Автоматическая регистрация** — Spring DI автоматически находит и регистрирует команды, callback и обработчики
* **Изолированность от библиотек** — только 2 класса зависят от Telegram API (`TelegramBot` и `KeyboardFactory`)
* **Легкое тестирование** — каждый компонент тестируется отдельно
* **Масштабируемость** — новые фичи добавляются без изменения существующего кода

### Тестирование:

Проект содержит **40 интеграционных тестов**, обеспечивающих полное покрытие функциональности:

* **CallbackHandlerIntegrationTest** (6 тестов) — тестирование обработки callback-запросов с реальными сервисами
* **StateMachineIntegrationTest** (8 тестов) — полные сценарии взаимодействия пользователя с ботом
* **TaskServiceIntegrationTest** (9 тестов) — операции с задачами
* **BotMessageProcessorTest** (3 теста) — обработка сообщений и callback
* **MessageRateLimiterTest** (4 теста) — защита от спама
* **UserServiceIntegrationTest** (3 теста) — управление пользователями
* **TelegramIdStateServiceIntegrationTest** (7 тестов) — управление состояниями пользователей

Все тесты являются интеграционными (`@SpringBootTest`) и работают с реальной конфигурацией Spring и H2 базой данных.

---

## Команды бота

| Команда   | Описание                              |
|-----------|---------------------------------------|
| `/start`  | Приветствие и справка по командам     |
| `/help`   | Справка по командам                   |
| `/add`    | Добавить задачу (интерактивный режим) |
| `/todo`   | Показать список невыполненных задач   |
| `/delete` | Удалить задачу (интерактивный режим)  |
| `/cancel` | Отменить текущее действие             |

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
│   ├── MessageHandler.java                # Интерфейс обработчика сообщений
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

## Как расширить функционал

### Добавить новую команду

1. Создайте класс, реализующий `BotCommand` в пакете `controller.command`:
   ```java
   @Component
   public class MyNewCommand implements BotCommand {
       @Override
       public String getCommandName() {
           return "/mycommand";
       }
       
       @Override
       public CommandResponse execute(String args, Long chatId) {
           // Ваша логика
           return new CommandResponse("Ответ", null, KeyboardType.MAIN_MENU);
       }
   }
   ```

2. Spring автоматически зарегистрирует команду через `CommandHandler`.

### Добавить новый callback

1. Создайте класс, реализующий `CallbackStrategy` в пакете `controller.callback`:
   ```java
   @Component
   public class MyCallbackStrategy implements CallbackStrategy {
       @Override
       public String getCallbackName() {
           return "MY_CALLBACK";
       }
       
       @Override
       public StateTransition handle(Long chatId) {
           return new StateTransition("Ответ", null, KeyboardType.MAIN_MENU);
       }
   }
   ```

2. Spring автоматически зарегистрирует callback через `CallbackHandler`.

### Добавить новое состояние

1. Добавьте состояние в enum `UserState`:
   ```java
   public enum UserState {
       DEFAULT,
       AWAITING_TASK_DESCRIPTION,
       AWAITING_TASK_ID_FOR_DELETION,
       MY_NEW_STATE  // Новое состояние
   }
   ```

2. Создайте обработчик, реализующий `MessageHandler` в пакете `state`:
   ```java
   @Component
   public class MyNewStateHandler implements MessageHandler {
       @Override
       public Optional<UserState> getHandledState() {
           return Optional.of(UserState.MY_NEW_STATE);
       }
       
       @Override
       public StateTransition handle(Long chatId, String text) {
           // Ваша логика обработки
           return new StateTransition("Ответ", UserState.DEFAULT);
       }
   }
   ```

3. Spring автоматически зарегистрирует обработчик в `StateMachine`.

### Добавить новый тип клавиатуры

1. Добавьте тип в enum `KeyboardType`:
   ```java
   public enum KeyboardType {
       MAIN_MENU,
       CANCEL,
       NONE,
       MY_KEYBOARD  // Новый тип
   }
   ```

2. Добавьте метод создания клавиатуры в `KeyboardService`:
   ```java
   public Keyboard buildMyKeyboard() {
       KeyboardButton button = new KeyboardButton("Текст", "CALLBACK_DATA");
       KeyboardRow row = new KeyboardRow(List.of(button));
       return new Keyboard(List.of(row));
   }
   ```

3. Зарегистрируйте клавиатуру в `KeyboardProvider`:
   ```java
   keyboardSuppliers.put(KeyboardType.MY_KEYBOARD, keyboardService::buildMyKeyboard);
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
Проект создан для демонстрации применения принципов SOLID и паттернов проектирования в реальном приложении.
