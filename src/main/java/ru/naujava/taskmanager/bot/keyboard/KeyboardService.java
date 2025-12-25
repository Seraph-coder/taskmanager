package ru.naujava.taskmanager.bot.keyboard;

import org.springframework.stereotype.Service;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.dto.Keyboard;
import ru.naujava.taskmanager.bot.dto.KeyboardButton;
import ru.naujava.taskmanager.bot.dto.KeyboardRow;
import ru.naujava.taskmanager.entity.UserState;

import java.util.List;

/**
 * Сервис для управления клавиатурами в зависимости от состояния.
 * <p>
 * Этот сервис определяет, какая клавиатура должна быть показана пользователю
 * в зависимости от его текущего состояния ({@link UserState}).
 * </p>
 *
 * @author Seraph-coder
 * @since 23.12.2025
 */
@Service
public class KeyboardService {

    /**
     * Возвращает клавиатуру для главного меню.
     *
     * @return {@link Keyboard} для главного меню.
     */
    public Keyboard buildMainMenu() {
        KeyboardButton add = new KeyboardButton(BotConstants.BUTTON_ADD_TASK, BotConstants.CALLBACK_ADD);
        KeyboardButton del = new KeyboardButton(BotConstants.BUTTON_DELETE_TASK, BotConstants.CALLBACK_DELETE);
        KeyboardButton list = new KeyboardButton(BotConstants.BUTTON_LIST_TASKS, BotConstants.CALLBACK_LIST);
        KeyboardRow row = new KeyboardRow(List.of(add, del, list));
        return new Keyboard(List.of(row));
    }

    /**
     * Возвращает клавиатуру для отмены действия.
     *
     * @return {@link Keyboard} для отмены действия.
     */
    public Keyboard buildCancelKeyboard() {
        KeyboardButton cancel = new KeyboardButton(BotConstants.BUTTON_CANCEL, BotConstants.CALLBACK_CANCEL);
        KeyboardRow row = new KeyboardRow(List.of(cancel));
        return new Keyboard(List.of(row));
    }
}

