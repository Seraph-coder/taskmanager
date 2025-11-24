package ru.naujava.taskmanager.controller.command;

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
    String execute(String message, Long chatId);
}
