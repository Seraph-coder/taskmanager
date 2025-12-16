package ru.naujava.taskmanager.statemachine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.service.UserService;
import ru.naujava.taskmanager.state.StateMachine;
import ru.naujava.taskmanager.state.StateTransition;

import java.util.Optional;

/**
 * Интеграционные тесты для {@link StateMachine}
 */
@SpringBootTest
@ActiveProfiles("test")
public class StateMachineIntegrationTest {
    @Autowired
    private StateMachine stateMachine;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    /**
     * Тест получения состояния пользователя, который существует и имеет состояние DEFAULT.
     * <br>
     * Ожидаемое поведение: возвращается Optional.empty(), поскольку DEFAULT не считается активным состоянием.
     */
    @Test
    @Transactional
    public void getUserStateSuccess() {
        userService.getOrCreateByTelegramId(2000L);

        Optional<UserState> stateOpt = stateMachine.getState(2000L);
        Assertions.assertTrue(stateOpt.isEmpty());
    }

    /**
     * Тест получения состояния пользователя, которого нет в репозитории.
     * <br>
     * Ожидаемое поведение: возвращается Optional.empty().
     */
    @Test
    @Transactional
    public void getUserStateNotFound() {
        Optional<UserState> userState = stateMachine.getState(2000L);
        Assertions.assertTrue(userState.isEmpty());
    }

    /**
     * Тест успешного изменения состояния пользователя.
     * <br>
     * Ожидаемое поведение: состояние пользователя обновляется в репозитории.
     */
    @Test
    @Transactional
    public void setUserStateSuccess() {
        userService.getOrCreateByTelegramId(2200L);
        stateMachine.setState(2200L, UserState.AWAITING_TASK_DESCRIPTION);
        Assertions.assertTrue(stateMachine.getState(2200L).isPresent());
    }

    /**
     * Тест успешного сброса состояния пользователя.
     * <br>
     * Ожидаемое поведение: состояние пользователя сбрасывается в репозитории.
     */
    @Test
    @Transactional
    public void resetUserStateSuccess() {
        userService.getOrCreateByTelegramId(2100L);
        stateMachine.setState(2100L, UserState.AWAITING_TASK_DESCRIPTION);
        Assertions.assertTrue(stateMachine.getState(2100L).isPresent());

        stateMachine.resetState(2100L);
        Assertions.assertTrue(stateMachine.getState(2100L).isEmpty());
    }

    /**
     * Тест обработки сообщения для добавления задачи через состояние.
     * <br>
     * Ожидаемое поведение: задача добавляется, состояние сбрасывается.
     */
    @Test
    @Transactional
    public void processMessageAddTaskSuccess() {
        userService.getOrCreateByTelegramId(2300L);
        stateMachine.setState(2300L, UserState.AWAITING_TASK_DESCRIPTION);

        StateTransition response = stateMachine.processMessage(2300L, "Новая задача");

        Assertions.assertEquals("Задача “Новая задача” добавлена", response.responseText());
        Assertions.assertEquals(UserState.DEFAULT, response.newState());
        Assertions.assertTrue(response.shouldSendMenu());
        Assertions.assertTrue(stateMachine.getState(2300L).isEmpty());
    }

    /**
     * Тест обработки сообщения для удаления задачи через состояние.
     * <br>
     * Ожидаемое поведение: задача удаляется, состояние сбрасывается.
     */
    @Test
    @Transactional
    public void processMessageDeleteTaskSuccess() {
        userService.getOrCreateByTelegramId(2400L);
        taskService.createTask("Задача для удаления", 2400L);
        stateMachine.setState(2400L, UserState.AWAITING_TASK_ID_FOR_DELETION);

        StateTransition response = stateMachine.processMessage(2400L, "1");

        Assertions.assertEquals("Задача “Задача для удаления” удалена", response.responseText());
        Assertions.assertEquals(UserState.DEFAULT, response.newState());
        Assertions.assertTrue(response.shouldSendMenu());
        Assertions.assertTrue(stateMachine.getState(2400L).isEmpty());
    }
}