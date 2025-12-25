package ru.naujava.taskmanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.entity.UserState;

/**
 * Интеграционные тесты для {@link TelegramIdStateService}.
 * <p>
 * Эти тесты проверяют корректность работы с состояниями пользователей,
 * включая создание, получение и изменение состояний.
 * </p>
 *
 * @author Seraph-coder
 * @since 25.12.2025
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class TelegramIdStateServiceIntegrationTest {

    @Autowired
    private TelegramIdStateService stateService;

    @Autowired
    private UserService userService;

    private static final long CHAT_ID = 123L;
    private static final long NON_EXISTENT_CHAT_ID = 999L;

    /**
     * Проверяет, что при первом обращении пользователя создается и пользователь, и его состояние.
     */
    @Test
    void getOrCreateUserState_createsUserAndState_whenUserDoesNotExist() {
        UserState state = stateService.getOrCreateUserState(CHAT_ID);

        Assertions.assertEquals(UserState.DEFAULT, state);
        Assertions.assertTrue(userService.findByTelegramId(CHAT_ID).isPresent());
    }

    /**
     * Проверяет, что при повторном обращении возвращается существующее состояние.
     */
    @Test
    void getOrCreateUserState_returnsExistingState_whenUserExists() {
        stateService.getOrCreateUserState(CHAT_ID);
        stateService.changeUserState(CHAT_ID, UserState.AWAITING_TASK_DESCRIPTION);

        UserState state = stateService.getOrCreateUserState(CHAT_ID);

        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, state);
    }

    /**
     * Проверяет, что при передаче null telegramId выбрасывается исключение.
     */
    @Test
    void getOrCreateUserState_throwsException_whenTelegramIdIsNull() {
        NullPointerException exception = Assertions.assertThrows(NullPointerException.class, ()
                -> stateService.getOrCreateUserState(null));
        Assertions.assertEquals("telegramId не может быть null", exception.getMessage());
    }

    /**
     * Проверяет успешное изменение состояния существующего пользователя.
     */
    @Test
    void changeUserState_updatesState_whenUserExists() {
        stateService.getOrCreateUserState(CHAT_ID);

        stateService.changeUserState(CHAT_ID, UserState.AWAITING_TASK_DESCRIPTION);

        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, stateService.getOrCreateUserState(CHAT_ID));
    }

    /**
     * Проверяет, что при попытке изменить состояние несуществующего пользователя,
     * пользователь и его состояние будут созданы.
     */
    @Test
    void changeUserState_createsAndUpdatesState_whenUserDoesNotExist() {
        stateService.changeUserState(NON_EXISTENT_CHAT_ID, UserState.AWAITING_TASK_DESCRIPTION);
        UserState state = stateService.getOrCreateUserState(NON_EXISTENT_CHAT_ID);
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, state);
        Assertions.assertTrue(userService.findByTelegramId(NON_EXISTENT_CHAT_ID).isPresent());
    }

    /**
     * Проверяет, что при передаче null telegramId при изменении состояния выбрасывается исключение.
     */
    @Test
    void changeUserState_throwsException_whenTelegramIdIsNull() {
        var exception = Assertions.assertThrows(NullPointerException.class, () ->
                stateService.changeUserState(null, UserState.DEFAULT));
        Assertions.assertEquals("telegramId не может быть null", exception.getMessage());
    }

    /**
     * Проверяет, что при передаче null состояния при изменении выбрасывается исключение.
     */
    @Test
    void changeUserState_throwsException_whenStateIsNull() {
        stateService.getOrCreateUserState(CHAT_ID);
        NullPointerException exception = Assertions.assertThrows(NullPointerException.class, () ->
                stateService.changeUserState(CHAT_ID, null));
        Assertions.assertEquals("newState не может быть null", exception.getMessage());
    }
}

