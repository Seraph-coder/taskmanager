package ru.naujava.taskmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.naujava.taskmanager.bot.TelegramBot;
import ru.naujava.taskmanager.bot.state.StateMachine;
import ru.naujava.taskmanager.controller.CommandHandler;

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
     * Создание экземпляра TelegramBot.
     */
    @Bean
    public TelegramBot telegramBot(CommandHandler commandHandler, StateMachine stateMachine) {
        return new TelegramBot(botToken, commandHandler, stateMachine);
    }
}