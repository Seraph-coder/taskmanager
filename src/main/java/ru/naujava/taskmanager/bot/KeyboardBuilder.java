package ru.naujava.taskmanager.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

/**
 * Строитель клавиатур для бота.
 *
 * @author Seraph-coder
 * @since 12.12.2025
 */
@Component
public class KeyboardBuilder {
    /**
     * Строит главное меню.
     */
    public InlineKeyboardMarkup buildMainMenu() {
        InlineKeyboardButton add = InlineKeyboardButton.builder()
                .text(BotConstants.BUTTON_ADD_TASK).callbackData(BotConstants.CALLBACK_ADD).build();
        InlineKeyboardButton del = InlineKeyboardButton.builder()
                .text(BotConstants.BUTTON_DELETE_TASK).callbackData(BotConstants.CALLBACK_DELETE).build();
        InlineKeyboardButton list = InlineKeyboardButton.builder()
                .text(BotConstants.BUTTON_LIST_TASKS).callbackData(BotConstants.CALLBACK_LIST).build();

        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(add);
        row.add(del);
        row.add(list);
        return new InlineKeyboardMarkup(List.of(row));
    }

    /**
     * Строит клавиатуру с кнопкой "Отменить".
     */
    public InlineKeyboardMarkup buildCancelKeyboard() {
        InlineKeyboardButton cancel = InlineKeyboardButton.builder()
                .text(BotConstants.BUTTON_CANCEL).callbackData(BotConstants.CALLBACK_CANCEL).build();
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(cancel);
        return new InlineKeyboardMarkup(List.of(row));
    }
}
