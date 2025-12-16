package ru.naujava.taskmanager.controller;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.naujava.taskmanager.entity.UserState;

/**
 * Ответ от команды, включая текст и действие.
 *
 * @author Seraph-coder
 * @since 11.12.2025
 */
public record CommandResponse(String text, Action action, UserState newState,
                              boolean shouldSendMenu, InlineKeyboardMarkup keyboard) {
    /**
     * Конструктор без клавиатуры и отправки меню.
     */
    public CommandResponse(String text, Action action, UserState newState) {
        this(text, action, newState, false, null);
    }

    /**
     * Конструктор без клавиатуры.
     */
    public CommandResponse(String text, Action action,
                           UserState newState, boolean shouldSendMenu) {
        this(text, action, newState, shouldSendMenu, null);
    }
}
