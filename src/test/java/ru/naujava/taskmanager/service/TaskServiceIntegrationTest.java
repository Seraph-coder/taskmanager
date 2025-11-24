package ru.naujava.taskmanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.naujava.taskmanager.builder.UserTestBuilder;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.entity.User;

import java.util.List;

/**
 * Тесты для сервиса задач.
 *
 * @author Seraph-coder
 * @since 14.11.2025
 */
@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceIntegrationTest {
    @Autowired
    private TaskService taskService;

    /**
     * Создает задачи и находит все задачи пользователя по его Telegram ID.
     * <br>
     * Ожидаемое поведение: Создает задачи и возвращает список задач, связанных с указанным Telegram ID.
     */
    @Test
    public void createTaskAndFindAllTasksByTelegramId() {
        User user = new UserTestBuilder().withId(1L).withTelegramId(1L).build();
        User anotherUser = new UserTestBuilder().withId(2L).withTelegramId(2L).build();

        taskService.createTask("Задача целевого пользователя", user.getTelegramId());
        taskService.createTask("Задача другого пользователя", anotherUser.getTelegramId());
        taskService.createTask("Задача целевого пользователя 2", user.getTelegramId());

        List<Task> tasks = taskService.findAllTasksByTelegramId(user.getTelegramId());

        Assertions.assertEquals(2, tasks.size());
        Assertions.assertTrue(tasks.stream()
                .map(Task::getDescription)
                .toList()
                .containsAll(List.of("Задача целевого пользователя", "Задача целевого пользователя 2")));
    }

    /**
     * Не находит задачи для несуществующего Telegram ID.
     * <br>
     * Ожидаемое поведение: возвращает пустой список.
     */
    @Test
    public void findAllTasksByNonExistentTelegramIdReturnsEmptyList() {
        User user = new UserTestBuilder().withId(1L).withTelegramId(1L).build();

        taskService.createTask("Задача другого пользователя", user.getTelegramId());

        List<Task> tasks = taskService.findAllTasksByTelegramId(999L);

        Assertions.assertTrue(tasks.isEmpty());
    }

    /**
     * Попытка создать задачу с таким же описанием для одного пользователя,
     * а также для разных пользователей.
     * <br>
     * Ожидаемое поведение: для одного пользователя выбрасывается IllegalArgumentException,
     * для разных пользователей задача создается успешно.
     */
    @Test
    public void createDuplicateTaskDescriptionForSameAndDifferentUsers() {
        User user = new UserTestBuilder().withId(1L).withTelegramId(1L).build();
        User anotherUser = new UserTestBuilder().withId(2L).withTelegramId(2L).build();
        taskService.createTask("Повторяющаяся задача", user.getTelegramId());
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.createTask("Повторяющаяся задача", user.getTelegramId()));
        Assertions.assertDoesNotThrow(() ->
                taskService.createTask("Повторяющаяся задача", anotherUser.getTelegramId()));
    }

    /**
     * Попытка создать задачу с null описанием или null Telegram ID.
     * <br>
     * Ожидаемое поведение: выбрасывается NullPointerException.
     */
    @Test
    public void createTaskWithNullDescriptionOrTelegramId() {
        User user = new UserTestBuilder().withId(1L).withTelegramId(1L).build();
        Assertions.assertThrows(NullPointerException.class, () ->
                taskService.createTask(null, user.getTelegramId())
        );
        Assertions.assertThrows(NullPointerException.class, () ->
                taskService.createTask("Какая-то задача", null)
        );
    }

    /**
     * Попытка удалить задачу по её ID и Telegram ID пользователя.
     * <br>
     * Ожидаемое поведение: задача удаляется успешно и возвращается удаленная задача.
     */
    @Test
    public void deleteTaskByIdAndTelegramId() {
        User user = new UserTestBuilder().withId(1L).withTelegramId(1L).build();
        Task task = taskService.createTask("Задача для удаления", user.getTelegramId());
        Task deletedTask = taskService.deleteTaskByIdAndTelegramId(task.getId(), user.getTelegramId());
        Assertions.assertEquals(task.getId(), deletedTask.getId());
        List<Task> tasks = taskService.findAllTasksByTelegramId(user.getTelegramId());
        Assertions.assertTrue(tasks.isEmpty());
    }

    /**
     * Попытка удалить несуществующую задачу.
     * <br>
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void deleteNonExistentTaskThrowsException() {
        User user = new UserTestBuilder().withId(1L).withTelegramId(1L).build();
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.deleteTaskByIdAndTelegramId(999L, user.getTelegramId()));
    }
}
