package ru.naujava.taskmanager.controller.commands;

import org.springframework.stereotype.Component;

/**
 * Команда помощи.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class HelpCommand implements BotCommand {
    @Override
    public String getCommandName() {
        return "/help";
    }

    @Override
    public String execute(String command, Long chatId) {
        return """
                Доступные команды:
                /help - показать эту справку
                /add <описание> - добавить задачу
                /todo - показать список задач
                /delete <taskId> - удалить задачу по id
                """;
    }
}
