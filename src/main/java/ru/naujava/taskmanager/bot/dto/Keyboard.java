package ru.naujava.taskmanager.bot.dto;

import java.util.List;

/**
 * DTO для клавиатуры.
 *
 * @param rows Список рядов кнопок.
 * @author Seraph-coder
 * @since 23.12.2025
 */
public record Keyboard(List<KeyboardRow> rows) {
}

