package ru.naujava.taskmanager.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.naujava.taskmanager.bot.TelegramBot;
import ru.naujava.taskmanager.controller.CommandHandler;

/**
 * Конфигурация Telegram бота.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
@Configuration
public class BotConfig {
    /**
     * Получение токена бота из переменных окружения.
     */
    @Bean
    public String botToken() {
        Dotenv dotenv = Dotenv.configure().load();
        return dotenv.get("TELEGRAM_BOT_TOKEN");
    }

    /**
     * Создание экземпляра TelegramBot.
     */
    @Bean
    public TelegramBot telegramBot(String botToken, CommandHandler commandHandler) {
        return new TelegramBot(botToken, commandHandler);
    }
}

