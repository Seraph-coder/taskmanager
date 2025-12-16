package ru.naujava.taskmanager.controller.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.controller.CommandResponse;
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

    /**
     * Конструктор добавления задачи.
     */
    public AddTaskCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getCommandName() {
        return "/add";
    }

    @Override
    public CommandResponse execute(String description, Long chatId) {
        if (chatId == null) {
            return new CommandResponse(
                    BotConstants.MSG_UNKNOWN_USER, Action.NONE, null);
        }
        if (description == null || description.isBlank()) {
            return new CommandResponse(
                    "Использование: /add <описание задачи>", Action.NONE,
                    null, true);
        }
        String trimDescription = description.trim();
        try {
            taskService.createTask(trimDescription, chatId);
            return new CommandResponse(
                    "Задача “" + trimDescription + "” добавлена",
                    Action.NONE, null, true);
        } catch (IllegalArgumentException e) {
            log.warn("Не удалось добавить задачу. Причина: {}", e.getMessage(), e);
            return new CommandResponse(
                    "Ошибка: " + e.getMessage(), Action.NONE,
                    null, true);
        }
    }
}