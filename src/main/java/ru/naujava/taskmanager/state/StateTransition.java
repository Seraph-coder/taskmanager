package ru.naujava.taskmanager.state;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.entity.UserState;

/**
 * Представляет переход состояния после обработки сообщения.
 * Содержит текст ответа, новое состояние (если переход), клавиатуру и действие.
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
public record StateTransition(String responseText, UserState newState,
                              InlineKeyboardMarkup keyboard, Action action, boolean shouldSendMenu) {

    /**
     * Конструктор с проверками.
     */
    public StateTransition {
        if (responseText == null || responseText.isBlank()) {
            throw new IllegalArgumentException("responseText не может быть null или пустым");
        }
        if (action == null) {
            action = Action.NONE;
        }
    }

    /**
     * Конструктор без клавиатуры, действия и shouldSendMenu.
     */
    public StateTransition(String responseText, UserState newState) {
        this(responseText, newState, null, Action.NONE, false);
    }

    /**
     * Конструктор без клавиатуры и действия, с shouldSendMenu.
     */
    public StateTransition(String responseText, UserState newState, boolean shouldSendMenu) {
        this(responseText, newState, null, Action.NONE, shouldSendMenu);
    }
}
