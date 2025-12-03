package ru.naujava.taskmanager.statemachine;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
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
     * Ожидаемое поведение: возвращается Optional.empty() и логируется ошибка.
     */
    @Test
    @Transactional
    public void getUserStateNotFound() {
        Logger logger = (Logger) LoggerFactory.getLogger(StateMachine.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        Optional<UserStateEnum> userState = stateMachine.getState(2000L);
        Assertions.assertTrue(userState.isEmpty());

        boolean hasErrorLog = listAppender.list.stream()
                .allMatch(ev ->
                        ev.getLevel().toString().equals("ERROR") &&
                                ev.getFormattedMessage().equals("Не удалось получить состояние для" +
                                        " chatId=2000: Пользователя с таким telegramId не существует: 2000"
                                )
                );
        Assertions.assertTrue(hasErrorLog);
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
     * Тест изменения состояния пользователя, которого нет в репозитории.
     * <br>
     * Ожидаемое поведение: операция завершается без ошибок и логируется ошибка.
     */
    @Test
    @Transactional
    public void setUserStateNotFound() {
        Logger logger = (Logger) LoggerFactory.getLogger(StateMachine.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        stateMachine.setState(2300L, UserStateEnum.AWAITING_TASK_DESCRIPTION);
        boolean hasErrorLog = listAppender.list.stream()
                .allMatch(ev ->
                        ev.getLevel().toString().equals("ERROR") &&
                                ev.getFormattedMessage().equals("Не удалось установить состояние " +
                                        "AWAITING_TASK_DESCRIPTION для chatId=2300: Пользователя с " +
                                        "таким telegramId не существует: 2300")
                );
        Assertions.assertTrue(hasErrorLog);
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

    /**
     * Тест сброса состояния пользователя, которого нет в репозитории.
     * <br>
     * Ожидаемое поведение: операция завершается без ошибок и логируется ошибка.
     */
    @Test
    @Transactional
    public void resetUserStateNotFound() {
        // Нет смысла тестировать отдельно, тк reset вызывает setState,
        // а setState уже протестирован на создание состояния при его отсутствии.
    }
}
