package ru.naujava.taskmanager.util;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.entity.Task;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Утилитарный компонент для форматирования и работы со списком задач.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
@Component
public class TaskListFormatter {
    public TaskListFormatter() {
    }

    /**
     * Возвращает список задач, отсортированных по ID.
     * Если список пуст — возвращает пустой список.
     */
    public List<Task> sortTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return List.of();
        }
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getId))
                .toList();
    }

    /**
     * Форматирует задачи в текст с нумерацией (1-based). Каждая запись в отдельной строке.
     * Если список пуст — возвращает пустую строку.
     */
    public String formatTasks(List<Task> tasks) {
        List<Task> sorted = sortTasks(tasks);
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

    /**
     * Получает задачу по её индексу в отсортированном списке задач (1-based).
     * Возвращает Optional.empty() при неверном индексе.
     */
    public Optional<Task> getTaskByIndex(List<Task> tasks, int index) {
        List<Task> sorted = sortTasks(tasks);
        if (index <= 0 || index > sorted.size()) {
            return Optional.empty();
        }
        return Optional.of(sorted.get(index - 1));
    }
}
