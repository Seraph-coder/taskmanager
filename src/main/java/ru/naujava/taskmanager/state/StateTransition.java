package ru.naujava.taskmanager.state;

import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;

/**
 * Переход состояния с ответом, новым состоянием и типом клавиатуры.
 *
 * @author Seraph-coder
 * @since 23.12.2025
 */
public record StateTransition(String responseText, UserState newState, KeyboardType keyboardType) {

    /**
     * Конструктор с проверками.
     */
    public StateTransition {
        if (responseText == null || responseText.isBlank()) {
            throw new IllegalArgumentException("responseText не может быть null или пустым");
        }
    }

    /**
     * Конструктор без клавиатуры.
     */
    public StateTransition(String responseText, UserState newState) {
        this(responseText, newState, KeyboardType.NONE);
    }
}
