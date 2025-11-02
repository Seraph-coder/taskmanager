package ru.naujava.taskmanager.controller.commands;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.util.TaskListFormatter;

import java.util.List;

/**
 * Команда для отображения списка задач пользователя.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class TodoListCommand implements BotCommand {
    private final TaskService taskService;

    public TodoListCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getCommandName() {
        return "/todo";
    }

    @Override
    public String execute(String command, Long chatId) {
        if (chatId == null) {
            return "Неизвестный пользователь";
        }
        List<Task> tasks = taskService.findAllTasksByTelegramId(chatId);
        if (tasks == null || tasks.isEmpty()) {
            return "Список задач пуст";
        }
        return TaskListFormatter.formatTasks(tasks);
    }
}
