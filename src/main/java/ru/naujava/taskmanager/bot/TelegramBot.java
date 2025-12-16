package ru.naujava.taskmanager.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

/**
 * TelegramBot реализует бота для Telegram,
 * который обрабатывает входящие сообщения и отвечает на них.
 * Поддерживает стандартные команды вида /command,
 * а также inline-кнопки и обработку CallbackQuery.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
public class TelegramBot implements SpringLongPollingBot, LongPollingUpdateConsumer {
    private final String botToken;
    private final TelegramClient telegramClient;
    private final BotMessageProcessor messageProcessor;
    private final Logger log = LoggerFactory.getLogger(TelegramBot.class);

    /**
     * Конструктор телеграм-бота.
     */
    public TelegramBot(String botToken, BotMessageProcessor messageProcessor) {
        this.botToken = botToken;
        this.messageProcessor = messageProcessor;
        if (botToken == null || botToken.isBlank()) {
            this.telegramClient = null;
        } else {
            this.telegramClient = new OkHttpTelegramClient(botToken);
        }
    }

    /**
     * Обрабатывает входящие обновления от Telegram.
     */
    @Override
    public void consume(List<Update> list) {
        for (Update update : list) {
            try {
                List<BotResponse> responses = messageProcessor.processUpdate(update);
                for (BotResponse response : responses) {
                    sendMessage(response);
                }
                if (update.hasCallbackQuery()) {
                    answerCallback(update.getCallbackQuery().getId());
                }
            } catch (Exception e) {
                log.warn("Ошибка при обработке обновления: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Отправляет сообщение.
     */
    @SuppressWarnings("rawtypes")
    private void sendMessage(BotResponse response) {
        SendMessage.SendMessageBuilder builder = SendMessage.builder()
                .chatId(response.chatId())
                .text(response.text());
        if (response.keyboard() != null) {
            builder.replyMarkup(response.keyboard());
        }
        executeSafe(builder.build());
    }

    /**
     * Безопасно выполняет отправку сообщения, обрабатывая исключения.
     */
    private void executeSafe(SendMessage msg) {
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            log.warn("Не удалось отправить сообщение: {}", e.getMessage(), e);
        }
    }

    /**
     * Отвечает на CallbackQuery.
     */
    private void answerCallback(String callbackId) {
        try {
            AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackId)
                    .build();
            telegramClient.execute(answer);
        } catch (TelegramApiException e) {
            log.warn("Не удалось ответить на callback query: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }
}
