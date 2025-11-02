# Task Manager — Telegram Bot

Простое приложение — Telegram-бот для управления личными задачами.
Проект написан на Java/Spring Boot и демонстрирует многослойную архитектуру: Controller → Service → Repository.

---

## Описание

Проект реализует базовый функционал управления задачами через Telegram-бота: добавление, просмотр и удаление задач.
Данные сохраняются в базе данных с использованием JPA/Hibernate.

---

## Архитектура

* **entity** — JPA-сущности (`Task`, `User`)
* **repository** — интерфейсы JPA (`TaskRepository`, `UserRepository`)
* **service** — бизнес-логика (`TaskService`, `UserService`)
* **controller** — обработка команд (`CommandHandler`, `CommandRegistry`, команды в `controller/commands`)
* **util** — вспомогательные утилиты (`TaskListFormatter`)
* **bot** — интеграция с Telegram API (`TelegramBot`)
* **config** — конфигурация бота (`BotConfig`)

---

## Команды бота

| Команда                  | Описание                            |
|--------------------------|-------------------------------------|
| `/start`                 | Приветствие и справка по командам   |
| `/help`                  | Справка по командам                 |
| `/add <описание>`        | Добавить задачу                     |
| `/todo`                  | Показать список невыполненных задач |
| `/delete <номер задачи>` | Удалить задачу по номеру            |

---

## Быстрый старт

### Требования

* Java 21
* Maven
* PostgreSQL

### Шаги

1. Склонируйте репозиторий:

   ```bash
   git clone https://github.com/yourname/taskmanager.git
   ```

2. Создайте файл `.env` в корне проекта и добавьте токен бота:

   ```env
   TELEGRAM_BOT_TOKEN=ваш_токен
   ```

   Проект использует библиотеку `dotenv-java` (см. `BotConfig`), которая читает значение `TELEGRAM_BOT_TOKEN`.
   Вместо `.env` можно задать переменную окружения `BOT_TOKEN`.

3. Настройте параметры подключения к базе данных
   В файле `src/main/resources/application.properties` укажите параметры PostgreSQL, например:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   spring.jpa.hibernate.ddl-auto=update
   ```

4. Соберите проект:

   ```bash
   mvn clean package
   ```

5. Запустите приложение:

   ```bash
   java -jar target/taskmanager.jar
   ```

---

## Примечания по тестам

* В тестах используется настройка `telegrambots.enabled=false` (см. `src/test/resources/application.properties`),
  чтобы избежать реальных подключений к Telegram при запуске тестов.
* Интеграционные тесты используют встроенную базу H2, которая создаётся и удаляется автоматически.

---

## Структура проекта

```
src/
 └── main/java/ru/naujava/taskmanager/
     ├── entity/            # JPA-сущности
     ├── repository/        # Репозитории
     ├── service/           # Бизнес-логика
     ├── controller/        # Обработка команд
     │    └── commands/     # Реализации команд
     ├── bot/               # Telegram-интеграция
     ├── config/            # Конфигурация бота
     └── util/              # Утилиты
```

---

## Где смотреть код

* Сущности: `src/main/java/ru/naujava/taskmanager/entity`
* Сервисы: `src/main/java/ru/naujava/taskmanager/service`
* Репозитории: `src/main/java/ru/naujava/taskmanager/repository`
* Команды бота: `src/main/java/ru/naujava/taskmanager/controller/commands`
* Контроллер команд: `src/main/java/ru/naujava/taskmanager/controller/CommandRegistry.java`
* Обработчик сообщений: `src/main/java/ru/naujava/taskmanager/controller/CommandHandler.java`
* Утилиты: `src/main/java/ru/naujava/taskmanager/util/TaskListFormatter.java`
* Telegram бот: `src/main/java/ru/naujava/taskmanager/bot/TelegramBot.java`
* Конфигурация бота: `src/main/java/ru/naujava/taskmanager/config/BotConfig.java`
* Тесты: `src/test/java/ru/naujava/taskmanager`