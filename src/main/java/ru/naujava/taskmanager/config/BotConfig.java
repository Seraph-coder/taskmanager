package ru.naujava.taskmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.naujava.taskmanager.bot.BotMessageProcessor;
import ru.naujava.taskmanager.bot.KeyboardBuilder;
import ru.naujava.taskmanager.bot.MessageRateLimiter;
import ru.naujava.taskmanager.bot.TelegramBot;
import ru.naujava.taskmanager.controller.CallbackHandler;
import ru.naujava.taskmanager.state.StateMachine;

/**
 * Конфигурация Telegram бота.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
@Configuration
public class BotConfig {
    @Value("${TELEGRAM_BOT_TOKEN:}")
    private String botToken;

    /**
     * Создание процессора сообщений.
     */
    @Bean
    public BotMessageProcessor botMessageProcessor(StateMachine stateMachine,
                                                   CallbackHandler callbackHandler,
                                                   KeyboardBuilder keyboardBuilder,
                                                   MessageRateLimiter rateLimiter) {
        return new BotMessageProcessor(stateMachine, callbackHandler, keyboardBuilder, rateLimiter);
    }

    /**
     * Создание экземпляра TelegramBot.
     */
    @Bean
    public TelegramBot telegramBot(BotMessageProcessor messageProcessor) {
        return new TelegramBot(botToken, messageProcessor);
    }
}