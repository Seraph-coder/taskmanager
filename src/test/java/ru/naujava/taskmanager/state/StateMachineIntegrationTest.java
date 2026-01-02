package ru.naujava.taskmanager.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.controller.CallbackHandler;
import ru.naujava.taskmanager.controller.CommandHandler;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.service.TelegramIdStateService;

/**
 * Интеграционные тесты для {@link StateMachine}.
 * <p>
 * Эти тесты проверяют полные сценарии взаимодействия пользователя с ботом,
 * включая переходы состояний, обработку команд и inline-кнопок.
 * </p>
 * <p>
 * ```java
 * Косвенно тестируемые классы: {@link CommandHandler}, {@link CallbackHandler},
 * {@link AwaitingTaskDescriptionHandler}, {@link AwaitingTaskIdForDeletionHandler}.
 * ```
 * </p>
 */
@SpringBootTest
@Transactional
class StateMachineIntegrationTest {

    @Autowired
    private StateMachine stateMachine;

    @Autowired
    private TelegramIdStateService stateService;

    @Autowired
    private TaskService taskService;

    private static final long CHAT_ID = 1L;

    /**
     * Проверяет полный успешный сценарий добавления задачи через команду /add.
     */
    @Test
    void testFullAddFlow_ViaCommand() {
        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/add");
        Assertions.assertEquals("Введите описание задачи", transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());

        transition = stateMachine.processMessage(CHAT_ID, "Купить молоко");
        Assertions.assertEquals("Задача 'Купить молоко' добавлена", transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());

        String taskList = taskService.formatTaskList(CHAT_ID);
        Assertions.assertEquals("1) Купить молоко", taskList);
    }

    /**
     * Проверяет полный успешный сценарий удаления задачи через команду /delete.
     */
    @Test
    void testFullDeleteFlow_ViaCommand() {
        taskService.createTask("Задача для удаления", CHAT_ID);

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/delete");
        Assertions.assertEquals("Введите номер задачи для удаления", transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_DELETION, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());

        transition = stateMachine.processMessage(CHAT_ID, "1");
        Assertions.assertEquals("Задача 'Задача для удаления' удалена", transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());

        String taskList = taskService.formatTaskList(CHAT_ID);
        Assertions.assertEquals("Список задач пуст", taskList);
    }

    /**
     * Проверяет сценарий отмены действия из состояния ожидания описания задачи.
     */
    @Test
    void testCancelFlow_FromAwaitingDescription() {
        stateMachine.processMessage(CHAT_ID, "/add");
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, stateService.getOrCreateUserState(CHAT_ID));

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/cancel");
        Assertions.assertEquals("Действие отменено", transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет полный успешный сценарий добавления задачи через inline-кнопку.
     */
    @Test
    void testAddFlow_ViaInlineButton() {
        StateTransition transition = stateMachine.processMessage(CHAT_ID, BotConstants.CALLBACK_ADD);
        Assertions.assertEquals("Введите описание задачи", transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());

        transition = stateMachine.processMessage(CHAT_ID, "Новая задача из inline");
        Assertions.assertEquals("Задача 'Новая задача из inline' добавлена", transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку неверного ввода при удалении задачи.
     */
    @Test
    void testDeleteFlow_InvalidInput() {
        taskService.createTask("Задача", CHAT_ID);
        stateMachine.processMessage(CHAT_ID, "/delete");

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "abc");
        Assertions.assertEquals("Неверный номер задачи. Попробуйте еще раз.", transition.responseText());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_DELETION, stateService.getOrCreateUserState(CHAT_ID));

        transition = stateMachine.processMessage(CHAT_ID, "99");
        Assertions.assertEquals("Задача с номером 99 не найдена. Попробуйте еще раз.",
                transition.responseText());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_DELETION, stateService.getOrCreateUserState(CHAT_ID));
    }

    /**
     * Проверяет реакцию на неизвестную команду.
     */
    @Test
    void testUnknownCommand() {
        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/unknown");
        Assertions.assertEquals("Неизвестная команда. Введите /help для списка команд",
                transition.responseText());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
    }

    /**
     * Проверяет сценарий, когда пользователь вводит /delete, находясь в процессе добавления задачи.
     */
    @Test
    void testMixedCommands_DeleteWhileAdding() {
        stateMachine.processMessage(CHAT_ID, "/add");
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, stateService.getOrCreateUserState(CHAT_ID));

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/delete");
        Assertions.assertEquals("Введите номер задачи для удаления", transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_DELETION, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
    }

    /**
     * Проверяет сценарий, когда пользователь вводит /add, находясь в процессе удаления задачи.
     */
    @Test
    void testMixedCommands_AddWhileDeleting() {
        taskService.createTask("some task", CHAT_ID);
        stateMachine.processMessage(CHAT_ID, "/delete");
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_DELETION, stateService.getOrCreateUserState(CHAT_ID));

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/add");
        Assertions.assertEquals("Введите описание задачи", transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
    }

    /**
     * Проверяет успешную отметку задачи как выполненной.
     */
    @Test
    void testDoneFlow_Success() {
        taskService.createTask("Task to complete", CHAT_ID);
        taskService.createTask("Another task", CHAT_ID);

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "DONE");
        Assertions.assertTrue(transition.responseText().contains("Task to complete"));
        Assertions.assertTrue(transition.responseText().contains("Another task"));
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_COMPLETION, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());

        transition = stateMachine.processMessage(CHAT_ID, "1");
        Assertions.assertTrue(transition.responseText().contains("Task to complete"));
        Assertions.assertTrue(transition.responseText().contains("отмечена как выполненная"));
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет отметку задачи как выполненной, когда все задачи уже выполнены.
     */
    @Test
    void testDoneFlow_NoUncompletedTasks() {
        StateTransition transition = stateMachine.processMessage(CHAT_ID, "DONE");
        Assertions.assertEquals("Список задач пуст", transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет просмотр выполненных задач.
     */
    @Test
    void testShowDoneFlow() {
        taskService.createTask("Completed task", CHAT_ID);
        taskService.markTaskCompletedByIndexAndTelegramId(1, CHAT_ID);

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "SHOWDONE");
        Assertions.assertTrue(transition.responseText().contains("Completed task"));
        Assertions.assertTrue(transition.responseText().contains("✓"));
        Assertions.assertNull(transition.newState());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет отмену операции отметки задачи.
     */
    @Test
    void testDoneFlow_Cancel() {
        taskService.createTask("Task", CHAT_ID);

        stateMachine.processMessage(CHAT_ID, "DONE");
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_COMPLETION, stateService.getOrCreateUserState(CHAT_ID));

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/cancel");
        Assertions.assertEquals("Действие отменено", transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку некорректного ввода при отметке задачи.
     */
    @Test
    void testDoneFlow_InvalidInput() {
        taskService.createTask("Task", CHAT_ID);

        stateMachine.processMessage(CHAT_ID, "DONE");

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "abc");
        Assertions.assertEquals("Неверный номер задачи. Попробуйте еще раз.", transition.responseText());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_COMPLETION, stateService.getOrCreateUserState(CHAT_ID));

        transition = stateMachine.processMessage(CHAT_ID, "99");
        Assertions.assertEquals("Задача с номером 99 не найдена. Попробуйте еще раз.",
                transition.responseText());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_COMPLETION, stateService.getOrCreateUserState(CHAT_ID));
    }
}
