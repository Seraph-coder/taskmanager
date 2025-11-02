package ru.naujava.taskmanager.util;

import ru.naujava.taskmanager.entity.Task;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Утилита для форматирования и работы со списком задач.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
public final class TaskListFormatter {
    private TaskListFormatter() {
    }

    /**
     * Возвращает список задач, отсортированных по ID.
     */
    public static List<Task> sortTasks(List<Task> tasks) {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getId))
                .toList();
    }

    /**
     * Форматирует задачи в текст с нумерацией (1-based). Каждая запись в отдельной строке.
     */
    public static String formatTasks(List<Task> tasks) {
        List<Task> sorted = sortTasks(tasks);
        return IntStream.range(0, sorted.size())
                .mapToObj(i -> (i + 1) + ") " + sorted.get(i).getDescription())
                .collect(Collectors.joining("\n"));
    }

    /**
     * Получает задачу по её индексу в отсортированном списке задач.
     */
    public static Optional<Task> getTaskByIndex(List<Task> tasks, int index) {
        List<Task> sorted = sortTasks(tasks);
        if (index <= 0 || index > sorted.size()) {
            return Optional.empty();
        }
        return Optional.of(sorted.get(index - 1));
    }
}
