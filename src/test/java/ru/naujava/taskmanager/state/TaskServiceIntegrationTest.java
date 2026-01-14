package ru.naujava.taskmanager.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.entity.User;
import ru.naujava.taskmanager.service.TaskService;

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
     * Создает задачи и находит все задачи пользователя по его User ID.
     * <br>
     * Ожидаемое поведение: Создает задачи и возвращает список задач, связанных с указанным User ID.
     */
    @Test
    public void createTaskAndFindAllTasksByUserId() {
        User user = new User(1L);
        User anotherUser = new User(2L);

        taskService.createTask("Задача целевого пользователя", user.getUserId());
        taskService.createTask("Задача другого пользователя", anotherUser.getUserId());
        taskService.createTask("Задача целевого пользователя 2", user.getUserId());

        List<Task> tasks = taskService.findAllTasksByUserId(user.getUserId());

        Assertions.assertEquals(2, tasks.size());
        Assertions.assertTrue(tasks.stream()
                .map(Task::getDescription)
                .toList()
                .containsAll(List.of("Задача целевого пользователя", "Задача целевого пользователя 2")));
    }

    /**
     * Не находит задачу для несуществующего User ID.
     * <br>
     * Ожидаемое поведение: возвращает пустой список.
     */
    @Test
    public void findAllTasksByNonExistentUserIdReturnsEmptyList() {
        User user = new User(1L);

        taskService.createTask("Задача другого пользователя", user.getUserId());

        List<Task> tasks = taskService.findAllTasksByUserId(999L);

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

        taskService.createTask("Повторяющаяся задача", user.getUserId());
        taskService.createTask("Повторяющаяся задача", anotherUser.getUserId());

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.createTask("Повторяющаяся задача", user.getUserId()));
        Assertions.assertEquals("Задача с описанием 'Повторяющаяся задача' уже существует",
                ex.getMessage());
    }

    /**
     * Попытка создать задачу с null описанием или null User ID.
     * <br>
     * Ожидаемое поведение: выбрасывается NullPointerException.
     */
    @Test
    public void createTaskWithNullDescriptionOrUserId() {
        User user = new User(1L);

        IllegalArgumentException ex0 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.createTask("", user.getUserId())
        );
        Assertions.assertEquals("Описание задачи не может быть пустым", ex0.getMessage());

        NullPointerException ex = Assertions.assertThrows(NullPointerException.class, () ->
                taskService.createTask(null, user.getUserId())
        );
        Assertions.assertEquals("taskDescription не должен быть null", ex.getMessage());

        NullPointerException ex2 = Assertions.assertThrows(NullPointerException.class, () ->
                taskService.createTask("Какая-то задача", null)
        );
        Assertions.assertEquals("userId не должен быть null", ex2.getMessage());
    }

    /**
     * Попытка удалить задачу по её ID и User ID пользователя.
     * <br>
     * Ожидаемое поведение: задача удаляется успешно и возвращается удаленная задача.
     */
    @Test
    public void deleteTaskByIdAndUserId() {
        User user = new User(3L);

        Task task = taskService.createTask("Задача для удаления", user.getUserId());
        Task deletedTask = taskService.deleteTaskByIdAndUserId(task.getId(), user.getUserId());

        Assertions.assertEquals(task.getId(), deletedTask.getId());
        List<Task> tasks = taskService.findAllTasksByUserId(user.getUserId());
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
                taskService.deleteTaskByIdAndUserId(999L, user.getUserId()));
        Assertions.assertEquals("Задача не найдена", ex.getMessage());
    }

    /**
     * Удаляет задачу по её номеру в списке задач пользователя.
     * <br>
     * Ожидаемое поведение: задача удаляется успешно и возвращается удаленная задача.
     */
    @Test
    public void deleteTaskByIndexAndUserId() {
        User user = new User(1001L);

        taskService.createTask("Задача 1", user.getUserId());
        taskService.createTask("Задача 2", user.getUserId());

        Task deletedTask = taskService.deleteTaskByIndexAndUserId(1, user.getUserId());
        Assertions.assertEquals("Задача 1", deletedTask.getDescription());

        List<Task> tasks = taskService.findAllTasksByUserId(user.getUserId());
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals("Задача 2", tasks.getFirst().getDescription());
    }

    /**
     * Попытка удалить задачу с некорректным индексом.
     * <br>
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void deleteTaskByInvalidIndexThrowsException() {
        User user = new User(1002L);

        taskService.createTask("Задача 1", user.getUserId());

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.deleteTaskByIndexAndUserId(5, user.getUserId()));
        Assertions.assertEquals("Ошибка: Задача с номером 5 не найдена", ex.getMessage());

        IllegalArgumentException ex2 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.deleteTaskByIndexAndUserId(0, user.getUserId()));
        Assertions.assertEquals("Номер задачи должен быть положительным", ex2.getMessage());

        IllegalArgumentException ex3 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.deleteTaskByIndexAndUserId(-1, user.getUserId()));
        Assertions.assertEquals("Номер задачи должен быть положительным", ex3.getMessage());
    }

    /**
     * Тест форматирования списка задач.
     * <br>
     * Ожидаемое поведение: возвращает отформатированную строку с задачами или сообщение о пустом списке.
     */
    @Test
    public void formatTaskList() {
        User user = new User(2001L);

        String result = taskService.formatUncompletedTaskAsString(user.getUserId());
        Assertions.assertEquals("Список задач пуст", result);

        taskService.createTask("Задача 1", user.getUserId());
        taskService.createTask("Задача 2", user.getUserId());

        result = taskService.formatUncompletedTaskAsString(user.getUserId());
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

        taskService.createTask("Какая-то задача", user.getUserId());
        Task task1 = taskService.createTask("Задача для выполнения", user.getUserId());
        taskService.createTask("Другая задача", user.getUserId());
        Task marked = taskService.markTaskCompletedByIndexAndUserId(2, user.getUserId());

        Assertions.assertEquals(task1.getId(), marked.getId());
        Assertions.assertTrue(marked.isDone());

        List<Task> uncompleted = taskService.getUncompletedTasks(user.getUserId());
        Assertions.assertEquals(2, uncompleted.size());
        Assertions.assertTrue(uncompleted.stream()
                .map(Task::getDescription)
                .toList()
                .containsAll(List.of("Какая-то задача", "Другая задача")));

        List<Task> completed = taskService.getCompletedTasks(user.getUserId());
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

        taskService.createTask("Невыполненная задача", user.getUserId());
        taskService.createTask("Выполненная задача", user.getUserId());
        taskService.markTaskCompletedByIndexAndUserId(2, user.getUserId());

        List<Task> completed = taskService.getCompletedTasks(user.getUserId());
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

        String result = taskService.formatCompletedTaskAsString(user.getUserId());
        Assertions.assertEquals("Список задач пуст", result);

        taskService.createTask("Задача 1", user.getUserId());
        taskService.createTask("Задача 2", user.getUserId());
        taskService.markTaskCompletedByIndexAndUserId(1, user.getUserId());
        taskService.markTaskCompletedByIndexAndUserId(1, user.getUserId());

        result = taskService.formatCompletedTaskAsString(user.getUserId());
        Assertions.assertEquals("1) Задача 1 ✓\n2) Задача 2 ✓", result);
    }

    /**
     * Попытка отметить задачу как выполненную с некорректным индексом.
     * <br>
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void markTaskCompletedByInvalidIndexThrowsException() {
        User user = new User(5002L);

        taskService.createTask("Задача 1", user.getUserId());

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.markTaskCompletedByIndexAndUserId(5, user.getUserId()));
        Assertions.assertEquals("Задача с номером 5 не найдена", ex.getMessage());

        IllegalArgumentException ex2 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.markTaskCompletedByIndexAndUserId(0, user.getUserId()));
        Assertions.assertEquals("Номер задачи должен быть положительным", ex2.getMessage());
    }

    /**
     * Проверяет форматирование списка задач для удаления: сначала выполненные, потом невыполненные.
     * Ожидаемое поведение: список форматируется с заголовками блоков.
     */
    @Test
    public void formatTaskListForDeletion_MixedTasks() {
        User user = new User(6001L);

        taskService.createTask("Задача 1", user.getUserId());
        taskService.createTask("Задача 2", user.getUserId());
        taskService.createTask("Задача 3", user.getUserId());
        taskService.markTaskCompletedByIndexAndUserId(1, user.getUserId());
        taskService.markTaskCompletedByIndexAndUserId(1, user.getUserId());

        String result = taskService.formatTaskListForDeletion(user.getUserId());
        String expected = "Выполненные задачи\n1) Задача 1 ✓\n2) Задача 2 ✓\n\nНевыполненные задачи\n3) Задача 3";
        Assertions.assertEquals(expected, result);
    }

    /**
     * Проверяет форматирование списка для удаления когда есть только выполненные задачи.
     * Ожидаемое поведение: показывается только блок выполненных задач.
     */
    @Test
    public void formatTaskListForDeletion_OnlyCompleted() {
        User user = new User(6002L);

        taskService.createTask("Задача 1", user.getUserId());
        taskService.createTask("Задача 2", user.getUserId());
        taskService.markTaskCompletedByIndexAndUserId(1, user.getUserId());
        taskService.markTaskCompletedByIndexAndUserId(1, user.getUserId());

        String result = taskService.formatTaskListForDeletion(user.getUserId());
        String expected = "Выполненные задачи\n1) Задача 1 ✓\n2) Задача 2 ✓";
        Assertions.assertEquals(expected, result);
    }

    /**
     * Проверяет форматирование списка для удаления когда есть только невыполненные задачи.
     * Ожидаемое поведение: показывается только блок невыполненных задач.
     */
    @Test
    public void formatTaskListForDeletion_OnlyUncompleted() {
        User user = new User(6003L);

        taskService.createTask("Задача 1", user.getUserId());
        taskService.createTask("Задача 2", user.getUserId());

        String result = taskService.formatTaskListForDeletion(user.getUserId());
        String expected = "Невыполненные задачи\n1) Задача 1\n2) Задача 2";
        Assertions.assertEquals(expected, result);
    }

    /**
     * Проверяет форматирование списка для удаления когда нет задач.
     * Ожидаемое поведение: возвращается сообщение о пустом списке.
     */
    @Test
    public void formatTaskListForDeletion_EmptyList() {
        User user = new User(6004L);

        String result = taskService.formatTaskListForDeletion(user.getUserId());
        Assertions.assertEquals("Список задач пуст", result);
    }

    /**
     * Проверяет удаление выполненной задачи из объединенного списка.
     * Ожидаемое поведение: задача удаляется корректно.
     */
    @Test
    public void deleteTaskByIndexFromCombinedList_DeleteCompleted() {
        User user = new User(6005L);

        taskService.createTask("Задача 1", user.getUserId());
        taskService.createTask("Задача 2", user.getUserId());
        taskService.createTask("Задача 3", user.getUserId());
        taskService.markTaskCompletedByIndexAndUserId(1, user.getUserId());

        Task deleted = taskService.deleteTaskByIndexFromCombinedList(1, user.getUserId());
        Assertions.assertEquals("Задача 1", deleted.getDescription());

        String list = taskService.formatTaskListForDeletion(user.getUserId());
        String expected = "Невыполненные задачи\n1) Задача 2\n2) Задача 3";
        Assertions.assertEquals(expected, list);
    }

    /**
     * Проверяет удаление невыполненной задачи из объединенного списка.
     * Ожидаемое поведение: задача удаляется корректно с учетом позиции после выполненных.
     */
    @Test
    public void deleteTaskByIndexFromCombinedList_DeleteUncompleted() {
        User user = new User(6006L);

        taskService.createTask("Задача 1", user.getUserId());
        taskService.createTask("Задача 2", user.getUserId());
        taskService.createTask("Задача 3", user.getUserId());
        taskService.markTaskCompletedByIndexAndUserId(1, user.getUserId());

        Task deleted = taskService.deleteTaskByIndexFromCombinedList(2, user.getUserId());
        Assertions.assertEquals("Задача 2", deleted.getDescription());

        String list = taskService.formatTaskListForDeletion(user.getUserId());
        String expected = "Выполненные задачи\n1) Задача 1 ✓\n\nНевыполненные задачи\n2) Задача 3";
        Assertions.assertEquals(expected, list);
    }

    /**
     * Проверяет удаление с некорректным индексом из объединенного списка.
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void deleteTaskByIndexFromCombinedList_InvalidIndex() {
        User user = new User(6007L);

        taskService.createTask("Задача 1", user.getUserId());

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                taskService.deleteTaskByIndexFromCombinedList(5, user.getUserId()));
        Assertions.assertEquals("Задача с номером 5 не найдена", ex.getMessage());
    }
}
