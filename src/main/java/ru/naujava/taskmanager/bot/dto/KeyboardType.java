package ru.naujava.taskmanager.bot.dto;

/**
 * Перечисление, представляющее типы клавиатур, которые может отображать бот.
 *
 * @author Seraph-coder
 * @since 23.12.2025
 */
public enum KeyboardType {
    /**
     * Нет клавиатуры.
     */
    NONE,
    /**
     * Главное меню.
     */
    MAIN_MENU,
    /**
     * Клавиатура с кнопкой "Отменить".
     */
    CANCEL
}

