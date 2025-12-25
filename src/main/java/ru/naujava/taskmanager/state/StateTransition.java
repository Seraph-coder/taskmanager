package ru.naujava.taskmanager.state;

import ru.naujava.taskmanager.bot.dto.KeyboardType;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.entity.UserState;

/**
 * Переход состояния с ответом, новым состоянием, типом клавиатуры и действием.
 *
 * @author Seraph-coder
 * @since 23.12.2025
 */
public record StateTransition(String responseText, UserState newState,
                              KeyboardType keyboardType, Action action) {

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
     * Конструктор без клавиатуры и действия.
     */
    public StateTransition(String responseText, UserState newState) {
        this(responseText, newState, KeyboardType.NONE, Action.NONE);
    }

    /**
     * Конструктор без действия.
     */
    public StateTransition(String responseText, UserState newState, KeyboardType keyboardType) {
        this(responseText, newState, keyboardType, Action.NONE);
    }
}
