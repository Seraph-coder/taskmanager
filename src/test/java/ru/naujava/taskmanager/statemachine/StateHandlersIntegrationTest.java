package ru.naujava.taskmanager.statemachine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.service.UserService;
import ru.naujava.taskmanager.state.StateMachine;
import ru.naujava.taskmanager.state.StateTransition;

import java.util.List;

/**
 * Интеграционные тесты для обработчика состояний стейт машины.
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
@SpringBootTest
@ActiveProfiles("test")
public class StateHandlersIntegrationTest {
    @Autowired
    private StateMachine stateMachine;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    /**
     * Тест успешного добавления задачи через состояние AWAITING_TASK_DESCRIPTION.
     * <br>
     * Ожидаемое поведение: задача добавляется, состояние сбрасывается, возвращается успешный ответ.
     */
    @Test
    @Transactional
    public void awaitingTaskDescriptionHandlerSuccess() {
        Long chatId = 3000L;
        userService.getOrCreateByTelegramId(chatId);
        stateMachine.setState(chatId, UserState.AWAITING_TASK_DESCRIPTION);

        StateTransition response = stateMachine.processMessage(chatId, "Новая задача");

        Assertions.assertEquals(UserState.DEFAULT, response.newState());
        Assertions.assertTrue(response.shouldSendMenu());
        Assertions.assertEquals("Задача “Новая задача” добавлена", response.responseText());

        List<Task> tasks = taskService.findAllTasksByTelegramId(chatId);
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals("Новая задача", tasks.getFirst().getDescription());
    }

    /**
     * Тест добавления задачи с существующим описанием через состояние AWAITING_TASK_DESCRIPTION.
     * <br>
     * Ожидаемое поведение: возвращается ошибка, состояние не сбрасывается.
     */
    @Test
    @Transactional
    public void awaitingTaskDescriptionHandlerDuplicateDescription() {
        Long chatId = 3001L;
        userService.getOrCreateByTelegramId(chatId);
        taskService.createTask("Существующая задача", chatId);
        stateMachine.setState(chatId, UserState.AWAITING_TASK_DESCRIPTION);

        StateTransition response = stateMachine.processMessage(chatId, "Существующая задача");

        Assertions.assertFalse(response.shouldSendMenu());
        Assertions.assertEquals("Задача с описанием 'Существующая задача' уже существует",
                response.responseText());
    }

    /**
     * Тест успешного удаления задачи по индексу через состояние AWAITING_TASK_ID_FOR_DELETION.
     * <br>
     * Ожидаемое поведение: задача удаляется, состояние сбрасывается, возвращается успешный ответ.
     */
    @Test
    @Transactional
    public void awaitingTaskIdForDeletionHandlerSuccess() {
        Long chatId = 3002L;
        userService.getOrCreateByTelegramId(chatId);
        taskService.createTask("Задача для удаления", chatId);
        stateMachine.setState(chatId, UserState.AWAITING_TASK_ID_FOR_DELETION);

        StateTransition response = stateMachine.processMessage(chatId, "1");

        Assertions.assertTrue(response.shouldSendMenu());
        Assertions.assertEquals("Задача “Задача для удаления” удалена", response.responseText());

        List<Task> tasks = taskService.findAllTasksByTelegramId(chatId);
        Assertions.assertTrue(tasks.isEmpty());
    }

    /**
     * Тест удаления задачи с неверным номером через состояние AWAITING_TASK_ID_FOR_DELETION.
     * <br>
     * Ожидаемое поведение: возвращается ошибка, состояние не сбрасывается.
     */
    @Test
    @Transactional
    public void awaitingTaskIdForDeletionHandlerInvalidIndex() {
        Long chatId = 3003L;
        userService.getOrCreateByTelegramId(chatId);
        stateMachine.setState(chatId, UserState.AWAITING_TASK_ID_FOR_DELETION);

        StateTransition response = stateMachine.processMessage(chatId, "abc");

        Assertions.assertFalse(response.shouldSendMenu());
        Assertions.assertEquals("Неверный номер задачи", response.responseText());
    }

    /**
     * Тест удаления несуществующей задачи через состояние AWAITING_TASK_ID_FOR_DELETION.
     * <br>
     * Ожидаемое поведение: возвращается ошибка, состояние не сбрасывается.
     */
    @Test
    @Transactional
    public void awaitingTaskIdForDeletionHandlerTaskNotFound() {
        Long chatId = 3004L;
        userService.getOrCreateByTelegramId(chatId);
        stateMachine.setState(chatId, UserState.AWAITING_TASK_ID_FOR_DELETION);

        StateTransition response = stateMachine.processMessage(chatId, "1");

        Assertions.assertFalse(response.shouldSendMenu());
        Assertions.assertEquals("Задача с номером 1 не найдена", response.responseText());
    }

    /**
     * Тест удаления задачи с нулевым индексом через состояние AWAITING_TASK_ID_FOR_DELETION.
     * <br>
     * Ожидаемое поведение: возвращается ошибка, состояние не сбрасывается.
     */
    @Test
    @Transactional
    public void awaitingTaskIdForDeletionHandlerZeroIndex() {
        Long chatId = 3005L;
        userService.getOrCreateByTelegramId(chatId);
        stateMachine.setState(chatId, UserState.AWAITING_TASK_ID_FOR_DELETION);

        StateTransition response = stateMachine.processMessage(chatId, "0");

        Assertions.assertFalse(response.shouldSendMenu());
        Assertions.assertEquals("Номер задачи должен быть положительным", response.responseText());
    }

    /**
     * Тест удаления задачи с отрицательным индексом через состояние AWAITING_TASK_ID_FOR_DELETION.
     * <br>
     * Ожидаемое поведение: возвращается ошибка, состояние не сбрасывается.
     */
    @Test
    @Transactional
    public void awaitingTaskIdForDeletionHandlerNegativeIndex() {
        Long chatId = 3007L;
        userService.getOrCreateByTelegramId(chatId);
        stateMachine.setState(chatId, UserState.AWAITING_TASK_ID_FOR_DELETION);

        StateTransition response = stateMachine.processMessage(chatId, "-1");

        Assertions.assertFalse(response.shouldSendMenu());
        Assertions.assertEquals("Номер задачи должен быть положительным", response.responseText());
    }

    /**
     * Тест добавления задачи с пустым текстом через состояние AWAITING_TASK_DESCRIPTION.
     * <br>
     * Ожидаемое поведение: возвращается ошибка, состояние не сбрасывается.
     */
    @Test
    @Transactional
    public void awaitingTaskDescriptionHandlerEmptyText() {
        Long chatId = 3006L;
        userService.getOrCreateByTelegramId(chatId);
        stateMachine.setState(chatId, UserState.AWAITING_TASK_DESCRIPTION);

        StateTransition response = stateMachine.processMessage(chatId, "");

        Assertions.assertFalse(response.shouldSendMenu());
        Assertions.assertEquals("taskDescription не должен быть пустым", response.responseText());
    }
}
