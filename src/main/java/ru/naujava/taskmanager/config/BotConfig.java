package ru.naujava.taskmanager.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.naujava.taskmanager.bot.NoOpTelegramBot;
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
     * Получение токена бота из переменных окружения или .env (если есть).
     * Не падает, если .env отсутствует.
     */
    public String botToken() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        String token = dotenv.get("TELEGRAM_BOT_TOKEN");
        if (token == null || token.isBlank()) {
            return null;
        }
        return token;
    }

    /**
     * Создание экземпляра TelegramBot. Если токен не задан — возвращаем NoOpTelegramBot,
     * безопасную заглушку для компиляции и запуска тестов без .env.
     */
    @Bean
    public TelegramBot telegramBot(CommandHandler commandHandler) {
        String botToken = botToken();

        if (botToken == null || botToken.isBlank()) {
            return new NoOpTelegramBot(null, commandHandler);
        }
        return new TelegramBot(botToken, commandHandler);
    }
}