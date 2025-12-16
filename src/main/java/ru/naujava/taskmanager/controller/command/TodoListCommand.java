package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.controller.CommandResponse;
import ru.naujava.taskmanager.service.TaskService;

/**
 * Команда для отображения списка задач пользователя.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class TodoListCommand implements BotCommand {
    private final TaskService taskService;

    /**
     * Конструктор отображения списка задач.
     */
    public TodoListCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getCommandName() {
        return "/todo";
    }

    @Override
    public CommandResponse execute(String command, Long chatId) {
        if (chatId == null) {
            return new CommandResponse(
                    BotConstants.MSG_UNKNOWN_USER,
                    Action.NONE, null, true);
        }
        String taskList = taskService.formatTaskList(chatId);
        return new CommandResponse(taskList, Action.NONE, null, true);
    }
}