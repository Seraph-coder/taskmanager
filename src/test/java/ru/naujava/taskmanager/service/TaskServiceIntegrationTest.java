package ru.naujava.taskmanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.entity.User;

import java.util.List;

/**
 * Тесты для сервиса задач {@link TaskService}.
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
        User user = new User(1L);
        User anotherUser = new User(2L);

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
     * Не находит задачу для несуществующего Telegram ID.
     * <br>
     * Ожидаемое поведение: возвращает пустой список.
     */
    @Test
    public void findAllTasksByNonExistentTelegramIdReturnsEmptyList() {
        User user = new User(1L);

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
        User user = new User(1L);
        User anotherUser = new User(2L);

        taskService.createTask("Повторяющаяся задача", user.getTelegramId());
        taskService.createTask("Повторяющаяся задача", anotherUser.getTelegramId());

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.createTask("Повторяющаяся задача", user.getTelegramId()));
        Assertions.assertEquals("Задача с описанием 'Повторяющаяся задача' уже существует",
                ex.getMessage());
    }

    /**
     * Попытка создать задачу с null описанием или null Telegram ID.
     * <br>
     * Ожидаемое поведение: выбрасывается NullPointerException.
     */
    @Test
    public void createTaskWithNullDescriptionOrTelegramId() {
        User user = new User(1L);

        NullPointerException ex = Assertions.assertThrows(NullPointerException.class, () ->
                taskService.createTask(null, user.getTelegramId())
        );
        Assertions.assertEquals("taskDescription не должен быть null", ex.getMessage());

        NullPointerException ex2 = Assertions.assertThrows(NullPointerException.class, () ->
                taskService.createTask("Какая-то задача", null)
        );
        Assertions.assertEquals("telegramId не должен быть null", ex2.getMessage());
    }

    /**
     * Попытка удалить задачу по её ID и Telegram ID пользователя.
     * <br>
     * Ожидаемое поведение: задача удаляется успешно и возвращается удаленная задача.
     */
    @Test
    public void deleteTaskByIdAndTelegramId() {
        User user = new User(1L);

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
        User user = new User(1L);

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.deleteTaskByIdAndTelegramId(999L, user.getTelegramId()));
        Assertions.assertEquals("Задача не найдена", ex.getMessage());
    }

    /**
     * Удаляет задачу по её номеру в списке задач пользователя.
     * <br>
     * Ожидаемое поведение: задача удаляется успешно и возвращается удаленная задача.
     */
    @Test
    public void deleteTaskByIndexAndTelegramId() {
        User user = new User(1001L);

        taskService.createTask("Задача 1", user.getTelegramId());
        Task task2 = taskService.createTask("Задача 2", user.getTelegramId());
        taskService.createTask("Задача 3", user.getTelegramId());

        Task deletedTask = taskService.deleteTaskByIndexAndTelegramId(2, user.getTelegramId());

        Assertions.assertEquals(task2.getId(), deletedTask.getId());
        List<Task> tasks = taskService.findAllTasksByTelegramId(user.getTelegramId());
        Assertions.assertEquals(2, tasks.size());
        Assertions.assertTrue(tasks.stream()
                .map(Task::getDescription)
                .toList()
                .containsAll(List.of("Задача 1", "Задача 3")));
    }

    /**
     * Попытка удалить задачу с некорректным индексом.
     * <br>
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void deleteTaskByInvalidIndexThrowsException() {
        User user = new User(1002L);

        taskService.createTask("Задача 1", user.getTelegramId());

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.deleteTaskByIndexAndTelegramId(5, user.getTelegramId()));
        Assertions.assertEquals("Ошибка: Задача 5 не найдена", ex.getMessage());

        IllegalArgumentException ex2 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.deleteTaskByIndexAndTelegramId(0, user.getTelegramId()));
        Assertions.assertEquals("Номер задачи должен быть положительным", ex2.getMessage());
    }

    /**
     * Тест форматирования списка задач.
     * <br>
     * Ожидаемое поведение: возвращает отформатированную строку с задачами или сообщение о пустом списке.
     */
    @Test
    public void formatTaskList() {
        User user = new User(2001L);

        String result = taskService.formatUncompletedTaskAsString(user.getTelegramId());
        Assertions.assertEquals("Список задач пуст", result);

        taskService.createTask("Задача 1", user.getTelegramId());
        taskService.createTask("Задача 2", user.getTelegramId());

        result = taskService.formatUncompletedTaskAsString(user.getTelegramId());
        Assertions.assertEquals("1) Задача 1\n2) Задача 2", result);
    }

    /**
     * Тест пометки задачи как выполненной. И ее удаление из списка невыполненных задач,
     * а также проверка, что она появляется в списке выполненных задач.
     * <br>
     * Ожидаемое поведение: задача помечается как выполненная.
     */
    @Test
    public void markTaskAsCompleted() {
        User user = new User(3001L);

        taskService.createTask("Какая-то задача", user.getTelegramId());
        Task task1 = taskService.createTask("Задача для выполнения", user.getTelegramId());
        taskService.createTask("Другая задача", user.getTelegramId());
        Task marked = taskService.markTaskCompletedByIndexAndTelegramId(2, user.getTelegramId());

        Assertions.assertEquals(task1.getId(), marked.getId());
        Assertions.assertTrue(marked.isDone());

        List<Task> uncompleted = taskService.getUncompletedTasks(user.getTelegramId());
        Assertions.assertEquals(2, uncompleted.size());
        Assertions.assertTrue(uncompleted.stream()
                .map(Task::getDescription)
                .toList()
                .containsAll(List.of("Какая-то задача", "Другая задача")));

        List<Task> completed = taskService.getCompletedTasks(user.getTelegramId());
        Assertions.assertEquals(1, completed.size());
        Assertions.assertEquals("Задача для выполнения", completed.getFirst().getDescription());
    }

    /**
     * Тест получения списка выполненных задач.
     * <br>
     * Ожидаемое поведение: возвращает только выполненные задачи.
     */
    @Test
    public void getCompletedTasks() {
        User user = new User(4001L);

        taskService.createTask("Невыполненная задача", user.getTelegramId());
        taskService.createTask("Выполненная задача", user.getTelegramId());
        taskService.markTaskCompletedByIndexAndTelegramId(2, user.getTelegramId());

        List<Task> completed = taskService.getCompletedTasks(user.getTelegramId());
        Assertions.assertEquals(1, completed.size());
        Assertions.assertEquals("Выполненная задача", completed.getFirst().getDescription());
        Assertions.assertTrue(completed.getFirst().isDone());
    }

    /**
     * Тест форматирования списка выполненных задач.
     * <br>
     * Ожидаемое поведение: возвращает отформатированную строку с выполненными задачами или сообщение о пустом списке.
     */
    @Test
    public void formatCompletedTaskList() {
        User user = new User(5001L);

        String result = taskService.formatCompletedTaskAsString(user.getTelegramId());
        Assertions.assertEquals("Список задач пуст", result);

        taskService.createTask("Задача 1", user.getTelegramId());
        taskService.createTask("Задача 2", user.getTelegramId());
        taskService.markTaskCompletedByIndexAndTelegramId(1, user.getTelegramId());
        taskService.markTaskCompletedByIndexAndTelegramId(1, user.getTelegramId());

        result = taskService.formatCompletedTaskAsString(user.getTelegramId());
        Assertions.assertEquals("1) Задача 1\n2) Задача 2", result);
    }

    /**
     * Попытка отметить задачу как выполненную с некорректным индексом.
     * <br>
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void markTaskCompletedByInvalidIndexThrowsException() {
        User user = new User(6001L);

        taskService.createTask("Задача 1", user.getTelegramId());

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.markTaskCompletedByIndexAndTelegramId(5, user.getTelegramId()));
        Assertions.assertEquals("Задача с номером 5 не найдена", ex.getMessage());

        IllegalArgumentException ex2 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.markTaskCompletedByIndexAndTelegramId(0, user.getTelegramId()));
        Assertions.assertEquals("Номер задачи должен быть положительным", ex2.getMessage());
    }
}
