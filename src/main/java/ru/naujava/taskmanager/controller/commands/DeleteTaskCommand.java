package ru.naujava.taskmanager.controller.commands;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.util.TaskListFormatter;

import java.util.List;
import java.util.Optional;

/**
 * Команда для удаления задачи по её номеру в списке.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class DeleteTaskCommand implements BotCommand {
    private final TaskService taskService;

    public DeleteTaskCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getCommandName() {
        return "/delete";
    }

    @Override
    public String execute(String taskId, Long chatId) {
        if (chatId == null) {
            return "Неизвестный пользователь";
        }
        if (taskId == null || taskId.isBlank()) {
            return "Использование: /delete <taskId>";
        }

        int taskIndex;

        try {
            taskIndex = Integer.parseInt(taskId.trim());
        } catch (NumberFormatException e) {
            return "Ошибка: номер задачи должен быть числом";
        }

        if (taskIndex <= 0) {
            return "Ошибка: номер задачи должен быть положительным числом";
        }

        List<Task> tasks = taskService.findAllTasksByTelegramId(chatId);
        if (tasks == null || tasks.isEmpty()) {
            return "Список задач пуст.";
        }

        Optional<Task> optionalTask = TaskListFormatter.getTaskByIndex(tasks, taskIndex);
        if (optionalTask.isEmpty()) {
            return "Ошибка: задача " + taskIndex + " не найдена";
        }

        Task toDelete = optionalTask.get();
        Task deleted = taskService.deleteTaskByIdAndTelegramId(toDelete.getId(), chatId);

        return "Задача “" + deleted.getDescription() + "” удалена";
    }
}
