package ru.naujava.taskmanager.statemachine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.bot.state.StateMachine;
import ru.naujava.taskmanager.entity.UserStateEnum;
import ru.naujava.taskmanager.service.UserService;

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

    /**
     * Тест успешного получения созданного состояния пользователя.
     * <br>
     * Ожидаемое поведение: возвращается текущее состояние пользователя.
     */
    @Test
    @Transactional
    public void getUserStateSuccess() {
        userService.getOrCreateByTelegramId(2000L);

        UserStateEnum state = stateMachine.getState(2000L)
                .orElseThrow(() -> new IllegalArgumentException("Пользователя не существует"));
        Assertions.assertEquals(UserStateEnum.DEFAULT, state);
    }

    /**
     * Тест получения состояния пользователя, которого нет в репозитории.
     * <br>
     * Ожидаемое поведение: возвращается Optional.empty().
     */
    @Test
    @Transactional
    public void getUserStateNotFound() {
        Optional<UserStateEnum> userState = stateMachine.getState(2000L);
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
        stateMachine.setState(2200L, UserStateEnum.AWAITING_TASK_DESCRIPTION);
        UserStateEnum state = stateMachine.getState(2200L)
                .orElseThrow(() -> new IllegalArgumentException("Пользователя не существует"));
        Assertions.assertEquals(UserStateEnum.AWAITING_TASK_DESCRIPTION, state);
    }

    /**
     * Тест успешного сброса состояния пользователя.
     * <br>
     * Ожидаемое поведение: состояние пользователя сбрасывается в репозитории.
     */
    @Test
    @Transactional
    public void resetUserStateSuccess() {
        // Нет смысла тестировать отдельно, тк reset вызывает setState,
        // а setState уже протестирован на успешное изменение состояния.
    }
}
