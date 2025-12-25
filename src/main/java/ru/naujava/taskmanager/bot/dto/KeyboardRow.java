package ru.naujava.taskmanager.bot.dto;

import java.util.List;

/**
 * DTO для ряда кнопок в клавиатуре.
 *
 * @param buttons Список кнопок в ряду.
 * @author Seraph-coder
 * @since 23.12.2025
 */
public record KeyboardRow(List<KeyboardButton> buttons) {
}

