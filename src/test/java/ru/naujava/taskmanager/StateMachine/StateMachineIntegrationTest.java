package ru.naujava.taskmanager.StateMachine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.bot.state.StateMachine;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.entity.UserStateEnum;
import ru.naujava.taskmanager.repository.UserStateRepository;
import ru.naujava.taskmanager.service.UserService;

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
    private UserStateRepository userStateRepository;

    /**
     * Тест успешного получения существующего состояния пользователя.
     * <br>
     * Ожидаемое поведение: возвращается текущее состояние пользователя.
     */
    @Test
    @Transactional
    public void getUserStateSuccess() {
        userService.getOrCreateByTelegramId(2000L);
        UserState s = new UserState(2000L);
        userStateRepository.save(s);

        UserStateEnum state = stateMachine.getState(2000L);
        Assertions.assertEquals(UserStateEnum.DEFAULT, state);
    }

    /**
     * Тест получения состояния пользователя, которого нет в репозитории.
     * <br>
     * Ожидаемое поведение: создается новое состояние пользователя со значением по умолчанию.
     */
    @Test
    @Transactional
    public void getUserStateNotFound() {
        userService.getOrCreateByTelegramId(2100L);
        UserStateEnum state = stateMachine.getState(2100L);
        Assertions.assertEquals(UserStateEnum.DEFAULT, state);
        Assertions.assertTrue(userStateRepository.existsById(2100L));
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
        stateMachine.setState(2200L, UserStateEnum.AWAITING_TASK_DESCRIPTION);
        UserStateEnum state = stateMachine.getState(2200L);
        Assertions.assertEquals(UserStateEnum.AWAITING_TASK_DESCRIPTION, state);
    }

    /**
     * Тест изменения состояния пользователя, которого нет в репозитории.
     * <br>
     * Ожидаемое поведение: создается новое состояние пользователя с указанным значением.
     */
    @Test
    @Transactional
    public void setUserStateNotFound() {
        userService.getOrCreateByTelegramId(2300L);
        stateMachine.setState(2300L, UserStateEnum.AWAITING_TASK_ID_FOR_DELETION);
        UserStateEnum state = stateMachine.getState(2300L);
        Assertions.assertEquals(UserStateEnum.AWAITING_TASK_ID_FOR_DELETION, state);
    }

    /**
     * Тест успешного сброса состояния пользователя.
     * <br>
     * Ожидаемое поведение: состояние пользователя сбрасывается в репозитории.
     */
    @Test
    @Transactional
    public void resetUserStateSuccess() {
        userService.getOrCreateByTelegramId(2400L);
        stateMachine.setState(2400L, UserStateEnum.AWAITING_TASK_DESCRIPTION);
        stateMachine.reset(2400L);
        UserStateEnum state = stateMachine.getState(2400L);
        Assertions.assertEquals(UserStateEnum.DEFAULT, state);
    }

    /**
     * Тест сброса состояния пользователя, которого нет в репозитории.
     * <br>
     * Ожидаемое поведение: операция завершается без ошибок.
     */
    @Test
    public void resetUserStateNotFound() {
        userService.getOrCreateByTelegramId(2500L);
        stateMachine.reset(2500L);
        Assertions.assertTrue(userStateRepository.existsById(2500L));
        Assertions.assertEquals(UserStateEnum.DEFAULT, stateMachine.getState(2500L));
    }
}
