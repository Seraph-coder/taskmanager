package ru.naujava.taskmanager.bot;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.naujava.taskmanager.controller.CommandHandler;

import java.util.List;

/**
 * TelegramBot реализует бота для Telegram,
 * который обрабатывает входящие сообщения и отвечает на них.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
public class TelegramBot implements SpringLongPollingBot, LongPollingUpdateConsumer {
    private final String botToken;
    private final TelegramClient telegramClient;
    private final CommandHandler commandHandler;

    public TelegramBot(String botToken, CommandHandler commandHandler) {
        this.botToken = botToken;
        this.commandHandler = commandHandler;
        if (botToken != null && !botToken.isBlank()) {
            this.telegramClient = new OkHttpTelegramClient(botToken);
        } else {
            this.telegramClient = null;
        }
    }

    /**
     * Обрабатывает входящие обновления от Telegram.
     */
    @Override
    public void consume(List<Update> list) {
        for (Update update : list) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Long chatId = update.getMessage().getChatId();
                String messageFromUser = update.getMessage().getText();
                String reply = commandHandler.handle(messageFromUser, chatId);
                if (reply != null && !reply.isEmpty()) {
                    sendMessage(chatId, reply);
                }
            }
        }
    }

    /**
     * Отправляет сообщение в указанный чат.
     */
    public void sendMessage(Long chatId, String reply) {
        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(chatId)
                .text(reply)
                .build();
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает токен бота.
     */
    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Получает клиент Telegram.
     */
    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }
}