package ru.naujava.taskmanager.bot.dto;

/**
 * DTO для кнопки клавиатуры.
 *
 * @param text         Текст кнопки.
 * @param callbackData Данные для callback.
 * @author Seraph-coder
 * @since 23.12.2025
 */
public record KeyboardButton(String text, String callbackData) {
}

