package ru.naujava.taskmanager.controller;

import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;

/**
 * Ответ от команды, включая текст, новое состояние и тип клавиатуры.
 *
 * @author Seraph-coder
 * @since 11.12.2025
 */
public record CommandResponse(String text, UserState newState, KeyboardType keyboardType) {
    /**
     * Конструктор без клавиатуры.
     */
    public CommandResponse(String text, UserState newState) {
        this(text, newState, KeyboardType.NONE);
    }
}
