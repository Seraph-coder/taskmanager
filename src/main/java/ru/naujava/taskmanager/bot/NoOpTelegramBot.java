package ru.naujava.taskmanager.bot;

import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.naujava.taskmanager.controller.CommandHandler;

import java.util.List;

/**
 * Заглушка Telegram бота, используется, когда токен не настроен.
 * Ничего не делает — нужна для успешных компиляции и тестов.
 */
public class NoOpTelegramBot extends TelegramBot {
    /**
     * Конструктор NoOpTelegramBot.
     */
    public NoOpTelegramBot(String botToken, CommandHandler commandHandler) {
        super(botToken, commandHandler);
    }

    /**
     * Получение потребителя обновлений (возвращает null).
     */
    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return null;
    }

    /**
     * Получение токена бота (возвращает "NO_TOKEN").
     */
    @Override
    public String getBotToken() {
        return "NO_TOKEN";
    }

    /**
     * Отправка сообщения (ничего не делает).
     */
    @Override
    public void sendMessage(Long chatId, String reply) {
    }

    /**
     * Обработка входящих обновлений (ничего не делает).
     */
    @Override
    public void consume(List<Update> list) {
    }
}