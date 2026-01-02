package ru.naujava.taskmanager.keyboard.model;

import java.util.List;

/**
 * Модель ряда кнопок в клавиатуре.
 *
 * @param buttons Список кнопок в ряду.
 * @author Seraph-coder
 * @since 23.12.2025
 */
public record KeyboardRow(List<KeyboardButton> buttons) {
}
