package ru.naujava.taskmanager.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.dto.KeyboardType;
import ru.naujava.taskmanager.controller.CallbackHandler;
import ru.naujava.taskmanager.controller.CommandHandler;
import ru.naujava.taskmanager.entity.UserState;
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
        Assertions.assertEquals(BotConstants.MSG_ENTER_TASK_DESCRIPTION, transition.responseText());
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
        String expectedText = "Выберите задачу для удаления:\n" +
                "Не выполненные задачи\n" +
                "1) Задача для удаления\n\n" +
                BotConstants.MSG_ENTER_TASK_NUMBER_DELETE;
        Assertions.assertEquals(expectedText, transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_DELETION, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());

        transition = stateMachine.processMessage(CHAT_ID, "1");
        Assertions.assertEquals("Задача 'Задача для удаления' удалена", transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());

        String taskList = taskService.formatTaskList(CHAT_ID);
        Assertions.assertEquals(BotConstants.MSG_TASKS_EMPTY, taskList);
    }

    /**
     * Проверяет сценарий отмены действия из состояния ожидания описания задачи.
     */
    @Test
    void testCancelFlow_FromAwaitingDescription() {
        stateMachine.processMessage(CHAT_ID, "/add");
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, stateService.getOrCreateUserState(CHAT_ID));

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/cancel");
        Assertions.assertEquals(BotConstants.MSG_ACTION_CANCELLED, transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет полный успешный сценарий добавления задачи через inline-кнопку.
     */
    @Test
    void testAddFlow_ViaInlineButton() {
        StateTransition transition = stateMachine.processMessage(CHAT_ID, BotConstants.CALLBACK_ADD);
        Assertions.assertEquals(BotConstants.MSG_ENTER_TASK_DESCRIPTION, transition.responseText());
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
        Assertions.assertEquals("Ошибка: Задача с номером 99 не найдена. Попробуйте еще раз.", transition.responseText());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_DELETION, stateService.getOrCreateUserState(CHAT_ID));
    }

    /**
     * Проверяет реакцию на неизвестную команду.
     */
    @Test
    void testUnknownCommand() {
        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/unknown");
        Assertions.assertEquals(BotConstants.MSG_UNKNOWN_COMMAND, transition.responseText());
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
        Assertions.assertEquals(BotConstants.MSG_TASKS_EMPTY, transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
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
        Assertions.assertEquals(BotConstants.MSG_ENTER_TASK_DESCRIPTION, transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
    }

    /**
     * Проверяет полный успешный сценарий отметки задачи выполненной через команду /done.
     */
    @Test
    void testFullDoneFlow_ViaCommand() {
        taskService.createTask("Задача для выполнения", CHAT_ID);
        taskService.createTask("Вторая задача", CHAT_ID);

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/done");
        String expectedText = "Ваши задачи:\n1) Задача для выполнения\n2) Вторая задача\n\n" +
                BotConstants.MSG_ENTER_TASK_NUMBER_COMPLETE;
        Assertions.assertEquals(expectedText, transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_COMPLETION, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());

        transition = stateMachine.processMessage(CHAT_ID, "1");
        Assertions.assertEquals("Задача 'Задача для выполнения' отмечена как выполненная", transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());

        String uncompletedList = taskService.formatUncompletedTaskAsString(CHAT_ID);
        Assertions.assertEquals("1) Вторая задача", uncompletedList);

        String completedList = taskService.formatCompletedTaskAsString(CHAT_ID);
        Assertions.assertEquals("1) Задача для выполнения ✓", completedList);
    }

    /**
     * Проверяет сценарий отметки задачи выполненной через inline-кнопку.
     */
    @Test
    void testDoneFlow_ViaInlineButton() {
        taskService.createTask("Задача", CHAT_ID);

        StateTransition transition = stateMachine.processMessage(CHAT_ID, BotConstants.CALLBACK_DONE);
        Assertions.assertTrue(transition.responseText().contains("Ваши задачи:"));
        Assertions.assertTrue(transition.responseText().contains(BotConstants.MSG_ENTER_TASK_NUMBER_COMPLETE));
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_COMPLETION, stateService.getOrCreateUserState(CHAT_ID));

        transition = stateMachine.processMessage(CHAT_ID, "1");
        Assertions.assertEquals("Задача 'Задача' отмечена как выполненная", transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
    }

    /**
     * Проверяет сценарий, когда список задач пуст при попытке отметить выполненной.
     */
    @Test
    void testDoneFlow_EmptyList() {
        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/done");
        Assertions.assertEquals(BotConstants.MSG_TASKS_EMPTY, transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет сценарий с неверным вводом при отметке задачи выполненной.
     */
    @Test
    void testDoneFlow_InvalidInput() {
        taskService.createTask("Задача", CHAT_ID);
        stateMachine.processMessage(CHAT_ID, "/done");

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "abc");
        Assertions.assertEquals("Неверный номер задачи. Попробуйте еще раз.", transition.responseText());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_COMPLETION, stateService.getOrCreateUserState(CHAT_ID));

        transition = stateMachine.processMessage(CHAT_ID, "99");
        Assertions.assertEquals("Задача с номером 99 не найдена. Попробуйте еще раз.", transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_COMPLETION, stateService.getOrCreateUserState(CHAT_ID));
    }

    /**
     * Проверяет сценарий отмены при отметке задачи выполненной.
     */
    @Test
    void testDoneFlow_Cancel() {
        taskService.createTask("Задача", CHAT_ID);
        stateMachine.processMessage(CHAT_ID, "/done");
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_COMPLETION, stateService.getOrCreateUserState(CHAT_ID));

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/cancel");
        Assertions.assertEquals(BotConstants.MSG_ACTION_CANCELLED, transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет команду /showdone для просмотра выполненных задач.
     */
    @Test
    void testShowDoneCommand() {
        taskService.createTask("Задача 1", CHAT_ID);
        taskService.createTask("Задача 2", CHAT_ID);
        taskService.markTaskCompletedByIndexAndTelegramId(1, CHAT_ID);

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/showdone");
        Assertions.assertEquals("1) Задача 1 ✓", transition.responseText());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет команду /showdone когда нет выполненных задач.
     */
    @Test
    void testShowDoneCommand_EmptyList() {
        taskService.createTask("Задача", CHAT_ID);

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/showdone");
        Assertions.assertEquals(BotConstants.MSG_TASKS_EMPTY, transition.responseText());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет, что /delete показывает задачи в правильном порядке: сначала выполненные, потом невыполненные.
     */
    @Test
    void testDeleteFlow_CombinedList() {
        taskService.createTask("Невыполненная 1", CHAT_ID);
        taskService.createTask("Невыполненная 2", CHAT_ID);
        taskService.createTask("Невыполненная 3", CHAT_ID);
        taskService.markTaskCompletedByIndexAndTelegramId(2, CHAT_ID);

        StateTransition transition = stateMachine.processMessage(CHAT_ID, "/delete");
        String response = transition.responseText();
        Assertions.assertTrue(response.contains("Выполненные задачи"));
        Assertions.assertTrue(response.contains("Невыполненная 2 ✓"));
        Assertions.assertTrue(response.contains("Не выполненные задачи"));
        Assertions.assertTrue(response.contains("Невыполненная 1"));
        Assertions.assertTrue(response.contains("Невыполненная 3"));
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_DELETION, stateService.getOrCreateUserState(CHAT_ID));

        transition = stateMachine.processMessage(CHAT_ID, "1");
        Assertions.assertEquals("Задача 'Невыполненная 2' удалена", transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, stateService.getOrCreateUserState(CHAT_ID));
    }
}
