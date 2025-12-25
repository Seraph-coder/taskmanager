package ru.naujava.taskmanager.bot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.naujava.taskmanager.bot.dto.Keyboard;
import ru.naujava.taskmanager.bot.dto.KeyboardButton;
import ru.naujava.taskmanager.bot.dto.KeyboardRow;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Фабрика для преобразования внутреннего представления клавиатуры в формат Telegram API.
 * <p>
 * Этот класс является единственным местом, которое зависит от {@link InlineKeyboardMarkup}
 * из библиотеки Telegram, инкапсулируя логику преобразования.
 * </p>
 *
 * @author Seraph-coder
 * @since 23.12.2025
 */
@Component
public class TelegramKeyboardFactory implements KeyboardFactory {
    @Override
    public InlineKeyboardMarkup build(Keyboard keyboard) {
        if (keyboard == null) {
            return null;
        }
        List<InlineKeyboardRow> rows = keyboard.rows().stream()
                .map(this::convertRow)
                .collect(Collectors.toList());
        return new InlineKeyboardMarkup(rows);
    }

    /**
     * Преобразует ряд кнопок из нашего внутреннего представления в формат Telegram API.
     */
    private InlineKeyboardRow convertRow(KeyboardRow row) {
        List<InlineKeyboardButton> buttons = row.buttons().stream()
                .map(this::convertButton)
                .collect(Collectors.toList());
        return new InlineKeyboardRow(buttons);
    }

    /**
     * Преобразует кнопку из нашего внутреннего представления в формат Telegram API.
     */
    private InlineKeyboardButton convertButton(KeyboardButton button) {
        return InlineKeyboardButton.builder()
                .text(button.text())
                .callbackData(button.callbackData())
                .build();
    }
}

