package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.util.TaskListSorter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Команда для отображения списка задач пользователя.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class TodoListCommand implements BotCommand {
    private final TaskService taskService;
    private final TaskListSorter taskListSorter;

    public TodoListCommand(TaskService taskService, TaskListSorter taskListSorter) {
        this.taskService = taskService;
        this.taskListSorter = taskListSorter;
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
        return formatTasks(tasks);
    }

    /**
     * Форматирует список задач в текст с нумерацией (1-based). Каждая запись в отдельной строке.
     * Если список пуст — возвращает пустую строку.
     * Использует метод сортировки из {@link TaskListSorter}
     *
     * @param tasks список задач
     * @return отформатированная строка с задачами
     */
    public String formatTasks(List<Task> tasks) {
        List<Task> sorted = taskListSorter.sortTasks(tasks);
        if (sorted.isEmpty()) {
            return "";
        }
        return IntStream.range(0, sorted.size())
                .mapToObj(i -> {
                    String desc = Optional.ofNullable(sorted.get(i).getDescription()).orElse("");
                    return (i + 1) + ") " + desc;
                })
                .collect(Collectors.joining("\n"));
    }
}