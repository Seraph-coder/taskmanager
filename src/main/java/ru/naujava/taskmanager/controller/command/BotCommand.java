package ru.naujava.taskmanager.controller.command;

import ru.naujava.taskmanager.controller.CommandResponse;

/**
 * Команды бота.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
public interface BotCommand {
    /**
     * Получить имя команды.
     */
    String getCommandName();

    /**
     * Выполнить команду.
     */
    CommandResponse execute(String message, Long chatId);
}
