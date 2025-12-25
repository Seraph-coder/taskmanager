package ru.naujava.taskmanager.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.dto.KeyboardType;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.state.StateTransition;

/**
 * Тесты для {@link CallbackHandler}.
 * <p>
 * Косвенно тестируемые классы: TaskService.
 * </p>
 *
 * @author Seraph-coder
 * @since 23.12.2025
 */
class CallbackHandlerTest {

    private CallbackHandler callbackHandler;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = Mockito.mock(TaskService.class);
        callbackHandler = new CallbackHandler(taskService);
    }

    /**
     * Проверяет обработку callback-запроса на добавление задачи.
     */
    @Test
    void handleAddCallback() {
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_ADD);
        Assertions.assertEquals(BotConstants.MSG_ENTER_TASK_DESCRIPTION, transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, transition.newState());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на просмотр списка задач.
     */
    @Test
    void handleListCallback() {
        Mockito.when(taskService.formatTaskList(1L)).thenReturn("1) Task 1");
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_LIST);
        Assertions.assertEquals("1) Task 1", transition.responseText());
        Assertions.assertNull(transition.newState());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на просмотр списка задач, когда список пуст.
     */
    @Test
    void handleListCallback_EmptyList() {
        Mockito.when(taskService.formatTaskList(1L)).thenReturn(BotConstants.MSG_TASKS_EMPTY);
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_LIST);
        Assertions.assertEquals(BotConstants.MSG_TASKS_EMPTY, transition.responseText());
        Assertions.assertNull(transition.newState());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на удаление задачи.
     */
    @Test
    void handleDeleteCallback() {
        Mockito.when(taskService.formatTaskListForDeletion(1L)).thenReturn("Выполненные задачи\n1) Task 1 ✓");
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_DELETE);
        Assertions.assertEquals("Выберите задачу для удаления:\nВыполненные задачи\n1) Task 1 ✓\n\n" +
                BotConstants.MSG_ENTER_TASK_NUMBER_DELETE, transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_DELETION, transition.newState());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на удаление, когда список задач пуст.
     */
    @Test
    void handleDeleteCallback_EmptyList() {
        Mockito.when(taskService.formatTaskListForDeletion(1L)).thenReturn(BotConstants.MSG_TASKS_EMPTY);
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_DELETE);
        Assertions.assertEquals(BotConstants.MSG_TASKS_EMPTY, transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, transition.newState());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на отметку задачи выполненной.
     */
    @Test
    void handleDoneCallback() {
        Mockito.when(taskService.formatUncompletedTaskAsString(1L)).thenReturn("1) Task 1");
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_DONE);
        Assertions.assertEquals("Ваши задачи:\n1) Task 1\n\n" +
                BotConstants.MSG_ENTER_TASK_NUMBER_COMPLETE, transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_COMPLETION, transition.newState());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на отметку задачи выполненной, когда список пуст.
     */
    @Test
    void handleDoneCallback_EmptyList() {
        Mockito.when(taskService.formatUncompletedTaskAsString(1L)).thenReturn(BotConstants.MSG_TASKS_EMPTY);
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_DONE);
        Assertions.assertEquals(BotConstants.MSG_TASKS_EMPTY, transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, transition.newState());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на просмотр выполненных задач.
     */
    @Test
    void handleShowDoneCallback() {
        Mockito.when(taskService.formatCompletedTaskAsString(1L)).thenReturn("1) Task 1 ✓");
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_SHOWDONE);
        Assertions.assertEquals("1) Task 1 ✓", transition.responseText());
        Assertions.assertNull(transition.newState());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на просмотр выполненных задач, когда список пуст.
     */
    @Test
    void handleShowDoneCallback_EmptyList() {
        Mockito.when(taskService.formatCompletedTaskAsString(1L)).thenReturn(BotConstants.MSG_TASKS_EMPTY);
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_SHOWDONE);
        Assertions.assertEquals(BotConstants.MSG_TASKS_EMPTY, transition.responseText());
        Assertions.assertNull(transition.newState());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на отмену действия.
     */
    @Test
    void handleCancelCallback() {
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_CANCEL);
        Assertions.assertEquals(BotConstants.MSG_ACTION_CANCELLED, transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, transition.newState());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку неизвестного callback-запроса.
     */
    @Test
    void handleUnknownCallback() {
        StateTransition transition = callbackHandler.handle(1L, "UNKNOWN_CALLBACK");
        Assertions.assertEquals("Неизвестный callback: UNKNOWN_CALLBACK", transition.responseText());
        Assertions.assertNull(transition.newState());
        Assertions.assertEquals(KeyboardType.NONE, transition.keyboardType());
    }
}
