package ru.naujava.taskmanager.keyboard;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.keyboard.model.Keyboard;
import ru.naujava.taskmanager.keyboard.model.KeyboardButton;
import ru.naujava.taskmanager.keyboard.model.KeyboardRow;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Провайдер клавиатур, который предоставляет клавиатуру по типу.
 * <br>
 * В будущем, при добавлении в проект ботов для других платформ,
 * можно будет добавить интерфейс провайдера и реализовать отдельные
 * провайдеры для каждой платформы.
 *
 * @author Seraph-coder
 * @since 02.01.2026
 */
@Component
public class KeyboardProvider {
    private final Map<KeyboardType, Supplier<Keyboard>> keyboardSuppliers;

    /**
     * Конструктор инициализирует Map с поставщиками клавиатур.
     */
    public KeyboardProvider() {
        this.keyboardSuppliers = new EnumMap<>(KeyboardType.class);
        keyboardSuppliers.put(KeyboardType.MAIN_MENU, this::createMainMenuKeyboard);
        keyboardSuppliers.put(KeyboardType.CANCEL, this::createCancelKeyboard);
        keyboardSuppliers.put(KeyboardType.NONE, () -> null);
    }

    /**
     * Возвращает клавиатуру для указанного типа.
     *
     * @param type тип клавиатуры
     * @return клавиатура или null для типа NONE
     */
    public Keyboard getKeyboard(KeyboardType type) {
        Supplier<Keyboard> supplier = keyboardSuppliers.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("Неизвестный тип клавиатуры: " + type);
        }
        return supplier.get();
    }

    /**
     * Возвращает клавиатуру главного меню.
     */
    private Keyboard createMainMenuKeyboard() {
        KeyboardButton add = new KeyboardButton(BotConstants.BUTTON_ADD_TASK, BotConstants.CALLBACK_ADD);
        KeyboardButton del = new KeyboardButton(BotConstants.BUTTON_DELETE_TASK, BotConstants.CALLBACK_DELETE);
        KeyboardRow row1 = new KeyboardRow(List.of(add, del));

        KeyboardButton done = new KeyboardButton(BotConstants.BUTTON_MARK_DONE, BotConstants.CALLBACK_DONE);
        KeyboardRow row2 = new KeyboardRow(List.of(done));

        KeyboardButton list = new KeyboardButton(BotConstants.BUTTON_LIST_TASKS, BotConstants.CALLBACK_LIST);
        KeyboardRow row3 = new KeyboardRow(List.of(list));

        KeyboardButton showDone = new KeyboardButton(BotConstants.BUTTON_SHOW_COMPLETED,
                BotConstants.CALLBACK_SHOWDONE);
        KeyboardRow row4 = new KeyboardRow(List.of(showDone));

        return new Keyboard(List.of(row1, row2, row3, row4));
    }

    /**
     * Возвращает клавиатуру для отмены действия.
     */
    private Keyboard createCancelKeyboard() {
        KeyboardButton cancel = new KeyboardButton(BotConstants.BUTTON_CANCEL, BotConstants.CALLBACK_CANCEL);
        KeyboardRow row = new KeyboardRow(List.of(cancel));
        return new Keyboard(List.of(row));
    }
}
