package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.service.TaskService;

import java.util.List;
import java.util.Objects;

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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            String desc = Objects.toString(tasks.get(i).getDescription(), "");
            sb.append(i + 1).append(") ").append(desc);
            if (i < tasks.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}