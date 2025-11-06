package ru.naujava.taskmanager.util;

import org.junit.jupiter.api.Test;
import ru.naujava.taskmanager.entity.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты для форматирования списка задач.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
public class TaskListFormatterTest {
    /**
     * Тест форматирования пустого списка задач.
     */
    @Test
    void formatEmptyListReturnsEmptyString() {
        TaskListFormatter formatter = new TaskListFormatter();
        String res = formatter.formatTasks(List.of());
        assertEquals("", res);
    }

    /**
     * Тест форматирования списка с двумя задачами.
     */
    @Test
    void formatTwoTasksSortedByIdAndNumbered() {
        TaskListFormatter formatter = new TaskListFormatter();

        Task t1 = new Task();
        t1.setId(10L);
        t1.setDescription("Позвонить врачу");

        Task t2 = new Task();
        t2.setId(5L);
        t2.setDescription("Купить хлеб");

        String res = formatter.formatTasks(List.of(t1, t2));
        String expected = "1) Купить хлеб\n2) Позвонить врачу";
        assertEquals(expected, res);
    }
}
