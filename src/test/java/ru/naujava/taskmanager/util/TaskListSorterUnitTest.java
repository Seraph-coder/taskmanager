package ru.naujava.taskmanager.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.naujava.taskmanager.builder.TaskTestBuilder;
import ru.naujava.taskmanager.entity.Task;

import java.util.List;

/**
 * Тесты для форматирования списка задач.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
public class TaskListSorterUnitTest {
    /**
     * Тест форматирования пустого списка задач.
     */
    @Test
    void formatEmptyListReturnsEmptyString() {
        TaskListSorter formatter = new TaskListSorter();
        List<Task> res = formatter.sortTasks(List.of());
        Assertions.assertEquals(List.of(), res);
    }

    /**
     * Тест форматирования списка с двумя задачами.
     */
    @Test
    void formatTwoTasksSortedByIdAndNumbered() {
        TaskListSorter formatter = new TaskListSorter();

        Task t1 = new TaskTestBuilder()
                .withId(10L).withDescription("Сделать домашнее задание").build();

        Task t2 = new TaskTestBuilder()
                .withId(5L).withDescription("Купить продукты").build();

        List<Task> res = formatter.sortTasks(List.of(t1, t2));
        Assertions.assertEquals(List.of(t2, t1), res);
    }
}