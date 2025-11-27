package ru.naujava.taskmanager.controller.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.util.TaskListSorter;

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
    private final Logger log = LoggerFactory.getLogger(DeleteTaskCommand.class);
    private final TaskService taskService;
    private final TaskListSorter taskListSorter;

    public DeleteTaskCommand(TaskService taskService, TaskListSorter taskListSorter) {
        this.taskService = taskService;
        this.taskListSorter = taskListSorter;
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
            log.error(e.getMessage());
            return "Ошибка: номер задачи должен быть числом";
        }

        if (taskIndex <= 0) {
            return "Ошибка: номер задачи должен быть положительным числом";
        }

        List<Task> tasks = taskService.findAllTasksByTelegramId(chatId);
        if (tasks == null || tasks.isEmpty()) {
            return "Список задач пуст.";
        }

        Optional<Task> optionalTask = getTaskByIndex(tasks, taskIndex);
        if (optionalTask.isEmpty()) {
            return "Ошибка: задача " + taskIndex + " не найдена";
        }

        Task toDelete = optionalTask.get();
        Task deleted = taskService.deleteTaskByIdAndTelegramId(toDelete.getId(), chatId);

        return "Задача “" + deleted.getDescription() + "” удалена";
    }

    /**
     * Получает задачу по её индексу в отсортированном списке задач (1-based).
     * Возвращает Optional.empty() при неверном индексе.
     * Использует метод сортировки из {@link TaskListSorter}
     */
    public Optional<Task> getTaskByIndex(List<Task> tasks, int index) {
        List<Task> sorted = taskListSorter.sortTasks(tasks);
        if (index <= 0 || index > sorted.size()) {
            return Optional.empty();
        }
        return Optional.of(sorted.get(index - 1));
    }
}
