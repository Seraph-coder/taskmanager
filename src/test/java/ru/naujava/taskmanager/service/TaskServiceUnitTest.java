package ru.naujava.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.entity.User;
import ru.naujava.taskmanager.repository.TaskRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit тесты для TaskService.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
public class TaskServiceUnitTest {

    private TaskRepository taskRepository;
    private UserService userService;
    private TaskService taskService;

    /**
     * Настройка перед каждым тестом.
     */
    @BeforeEach
    void setup() {
        taskRepository = mock(TaskRepository.class);
        userService = mock(UserService.class);
        taskService = new TaskService(taskRepository, userService);
    }

    /**
     * Тест успешного создания задачи.
     */
    @Test
    void createTask_successful() {
        when(taskRepository.existsByUser_TelegramIdAndDescription(1L, "Купить хлеб")).thenReturn(false);
        User u = new User();
        u.setId(2L);
        u.setTelegramId(1L);
        when(userService.getOrCreateByTelegramId(1L)).thenReturn(u);

        Task saved = new Task();
        saved.setId(100L);
        saved.setDescription("Купить хлеб");
        when(taskRepository.save(any())).thenReturn(saved);

        Task result = taskService.createTask("Купить хлеб", 1L);
        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(taskRepository).save(any(Task.class));
    }

    /**
     * Тест создания дублирующей задачи.
     */
    @Test
    void createTask_duplicate_throws() {
        when(taskRepository.existsByUser_TelegramIdAndDescription(1L, "Купить хлеб")).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask("Купить хлеб", 1L));
        assertEquals("Задача с описанием 'Купить хлеб' уже существует", ex.getMessage());
        verify(taskRepository, never()).save(any());
    }

    /**
     * Тест успешного удаления задачи.
     */
    @Test
    void deleteTask_successful() {
        Task t = new Task();
        t.setId(10L);
        when(taskRepository.findByIdAndUser_TelegramId(10L, 1L)).thenReturn(Optional.of(t));

        Task res = taskService.deleteTaskByIdAndTelegramId(10L, 1L);
        assertEquals(10L, res.getId());
        verify(taskRepository).delete(t);
    }

    /**
     * Тест удаления несуществующей задачи.
     */
    @Test
    void deleteTask_notFound_throws() {
        when(taskRepository.findByIdAndUser_TelegramId(99L, 1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> taskService.deleteTaskByIdAndTelegramId(99L, 1L));
    }
}

