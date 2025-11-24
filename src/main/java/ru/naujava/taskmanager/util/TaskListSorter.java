package ru.naujava.taskmanager.util;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.entity.Task;

import java.util.Comparator;
import java.util.List;

/**
 * Утилитарный класс для сортировки списка задач.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
@Component
public class TaskListSorter {
    /**
     * Конструктор по умолчанию.
     * Создает экземпляр TaskListFormatter без состояния.
     */
    public TaskListSorter() {
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
}
