package ru.naujava.taskmanager.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.controller.callback.*;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.state.StateTransition;

import java.util.List;

/**
 * Тесты для {@link CallbackHandler}.
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

        // Создаем список стратегий
        List<CallbackStrategy> strategies = List.of(
                new AddTaskCallbackStrategy(),
                new DeleteTaskCallbackStrategy(taskService),
                new ListTasksCallbackStrategy(taskService),
                new CancelCallbackStrategy()
        );

        callbackHandler = new CallbackHandler(strategies);
    }

    /**
     * Проверяет обработку callback-запроса на добавление задачи.
     */
    @Test
    void handleAddCallback() {
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_ADD);
        Assertions.assertEquals("Введите описание задачи", transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, transition.newState());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на удаление задачи.
     */
    @Test
    void handleDeleteCallback() {
        Mockito.when(taskService.formatTaskList(1L)).thenReturn("1) Task 1");
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_DELETE);
        Assertions.assertEquals("Ваши задачи:\n1) Task 1\n\nВведите номер задачи для удаления",
                transition.responseText());
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_DELETION, transition.newState());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на удаление, когда список задач пуст.
     */
    @Test
    void handleDeleteCallback_EmptyList() {
        Mockito.when(taskService.formatTaskList(1L)).thenReturn(BotConstants.MSG_TASKS_EMPTY);
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_DELETE);
        Assertions.assertEquals("Список задач пуст", transition.responseText());
        Assertions.assertEquals(UserState.DEFAULT, transition.newState());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на отмену действия.
     */
    @Test
    void handleCancelCallback() {
        StateTransition transition = callbackHandler.handle(1L, BotConstants.CALLBACK_CANCEL);
        Assertions.assertEquals("Действие отменено", transition.responseText());
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
