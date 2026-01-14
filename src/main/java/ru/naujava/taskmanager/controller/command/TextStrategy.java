package ru.naujava.taskmanager.controller.command;

import ru.naujava.taskmanager.controller.CommandResponse;

/**
 * Интерфейс стратегии для обработки callback-команд.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
public interface TextStrategy {
    /**
     * Получить имя текстовой команды.
     */
    String getCommandName();

    /**
     * Выполнить команду.
     */
    CommandResponse execute(String message, Long chatId);
}
