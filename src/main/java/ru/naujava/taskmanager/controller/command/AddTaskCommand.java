package ru.naujava.taskmanager.controller.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.service.TaskService;

/**
 * Команда для добавления новой задачи.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class AddTaskCommand implements BotCommand {
    private final Logger log = LoggerFactory.getLogger(AddTaskCommand.class);
    private final TaskService taskService;

    public AddTaskCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getCommandName() {
        return "/add";
    }

    @Override
    public String execute(String description, Long chatId) {
        if (chatId == null) {
            return "Неизвестный пользователь";
        }
        if (description == null || description.isBlank()) {
            return "Использование: /add <описание задачи>";
        }
        String trimDescription = description.trim();
        try {
            taskService.createTask(trimDescription, chatId);
            return "Задача “" + trimDescription + "” добавлена";
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return "Ошибка: " + e.getMessage();
        }
    }
}