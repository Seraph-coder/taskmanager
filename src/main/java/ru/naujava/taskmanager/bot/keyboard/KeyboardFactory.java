package ru.naujava.taskmanager.bot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.naujava.taskmanager.keyboard.model.Keyboard;
import ru.naujava.taskmanager.keyboard.model.KeyboardButton;
import ru.naujava.taskmanager.keyboard.model.KeyboardRow;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Фабрика для преобразования внутреннего представления клавиатуры в формат Telegram API.
 * <p>
 * Этот класс является единственным местом, помимо {@link ru.naujava.taskmanager.bot.TelegramBot},
 * которое зависит от библиотеки Telegram Bots.
 * </p>
 *
 * @author Seraph-coder
 * @since 23.12.2025
 */
@Component
public class KeyboardFactory {

    /**
     * Собирает {@link InlineKeyboardMarkup} из нашего собственного {@link Keyboard}.
     *
     * @param keyboard Наша внутренняя модель клавиатуры.
     * @return Готовая клавиатура для Telegram API.
     */
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
