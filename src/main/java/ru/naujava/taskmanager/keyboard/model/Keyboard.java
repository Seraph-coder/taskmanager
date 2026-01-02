package ru.naujava.taskmanager.keyboard.model;

import java.util.List;

/**
 * Модель клавиатуры.
 *
 * @param rows Список рядов кнопок.
 * @author Seraph-coder
 * @since 23.12.2025
 */
public record Keyboard(List<KeyboardRow> rows) {
}
