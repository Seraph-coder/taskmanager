package ru.naujava.taskmanager.controller;

import ru.naujava.taskmanager.bot.dto.KeyboardType;
import ru.naujava.taskmanager.entity.UserState;

/**
 * Ответ от команды, включая текст и действие.
 *
 * @author Seraph-coder
 * @since 11.12.2025
 */
public record CommandResponse(String text, Action action, UserState newState,
                              KeyboardType keyboardType) {
    /**
     * Конструктор без клавиатуры и отправки меню.
     */
    public CommandResponse(String text, Action action, UserState newState) {
        this(text, action, newState, KeyboardType.NONE);
    }
}
