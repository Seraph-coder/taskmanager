package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;

/**
 * Команда старта.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class StartCommand implements BotCommand {
    @Override
    public String getCommandName() {
        return "/start";
    }

    @Override
    public String execute(String message, Long chatId) {
        return """
                Здравствуйте! Я бот, который поможет вам управлять задачами.
                    Доступные команды:
                    /add [описание] – добавить задачу
                    /todo – показать список задач
                    /delete [номер] – удалить задачу
                    /help – справка по командам
                """;
    }
}
