package ru.naujava.taskmanager.bot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.naujava.taskmanager.bot.dto.Keyboard;

/**
 * Интерфейс для фабрики клавиатур.
 *
 * @author Seraph-coder
 * @since 23.12.2025
 */
public interface KeyboardFactory {
    /**
     * Собирает клавиатуру для конкретной платформы из нашего внутреннего представления.
     *
     * @param keyboard Наша внутренняя модель клавиатуры.
     * @return Готовая клавиатура для платформы.
     */
    InlineKeyboardMarkup build(Keyboard keyboard);
}

