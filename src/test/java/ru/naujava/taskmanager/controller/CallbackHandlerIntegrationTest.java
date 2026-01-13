package ru.naujava.taskmanager.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.CallbackHandler;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.state.StateTransition;

/**
 * Интеграционные тесты для {@link CallbackHandler}.
 * <p>
 * Тестируют полное взаимодействие CallbackHandler со стратегиями и сервисами.
 * </p>
 *
 * @author Seraph-coder
 * @since 23.12.2025
 */
@SpringBootTest
class CallbackHandlerIntegrationTest {

    @Autowired
    private CallbackHandler callbackHandler;

    @Autowired
    private TaskService taskService;

    private static final long CHAT_ID = 1L;

    /**
     * Проверяет обработку callback-запроса на добавление задачи.
     */
    @Test
    void handleAddCallback() {
        StateTransition transition = callbackHandler.processCallback(BotConstants.CALLBACK_ADD, CHAT_ID);

        Assertions.assertNotNull(transition);
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, transition.newState());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на удаление задачи.
     */
    @Test
    void handleDeleteCallback() {
        taskService.createTask("Тестовая задача", CHAT_ID);

        StateTransition transition = callbackHandler.processCallback(BotConstants.CALLBACK_DELETE, CHAT_ID);

        Assertions.assertNotNull(transition);
        Assertions.assertEquals(UserState.AWAITING_TASK_ID_FOR_DELETION, transition.newState());
        Assertions.assertEquals(KeyboardType.CANCEL, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на удаление, когда задач нет.
     */
    @Test
    void handleDeleteCallback_NoTasks() {
        StateTransition transition = callbackHandler.processCallback(BotConstants.CALLBACK_DELETE, CHAT_ID);

        Assertions.assertNotNull(transition);
        Assertions.assertEquals(UserState.DEFAULT, transition.newState());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на отмену действия.
     */
    @Test
    void handleCancelCallback() {
        StateTransition transition = callbackHandler.processCallback(BotConstants.CALLBACK_CANCEL, CHAT_ID);

        Assertions.assertNotNull(transition);
        Assertions.assertEquals(UserState.DEFAULT, transition.newState());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку callback-запроса на список задач.
     */
    @Test
    void handleListCallback() {
        taskService.createTask("Задача 1", CHAT_ID);

        StateTransition transition = callbackHandler.processCallback(BotConstants.CALLBACK_LIST, CHAT_ID);

        Assertions.assertNotNull(transition);
        Assertions.assertNull(transition.newState());
        Assertions.assertEquals(KeyboardType.MAIN_MENU, transition.keyboardType());
    }

    /**
     * Проверяет обработку неизвестного callback-запроса.
     */
    @Test
    void handleUnknownCallback() {
        StateTransition transition = callbackHandler.processCallback("UNKNOWN_CALLBACK", CHAT_ID);

        Assertions.assertNotNull(transition);
        Assertions.assertNull(transition.newState());
        Assertions.assertEquals(KeyboardType.NONE, transition.keyboardType());
    }
}
