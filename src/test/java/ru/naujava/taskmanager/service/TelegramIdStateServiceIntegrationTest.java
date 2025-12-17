package ru.naujava.taskmanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.repository.TelegramIdStateRepository;

/**
 * Тесты для сервиса управления состояниями пользователей {@link TelegramIdStateService}.
 *
 * @author Seraph-coder
 * @since 26.11.2025
 */
@SpringBootTest
@ActiveProfiles("test")
public class TelegramIdStateServiceIntegrationTest {
    @Autowired
    private TelegramIdStateRepository telegramIdStateRepository;

    @Autowired
    private TelegramIdStateService telegramIdStateService;

    @Autowired
    private UserService userService;

    /**
     * Тест создания нового состояния пользователя, которого еще нет в репозитории.
     * <br>
     * Ожидаемое поведение: создается новое состояние пользователя со значением DEFAULT.
     */
    @Test
    @Transactional
    public void getUserStateCreatesNewIfNotExists() {
        userService.getOrCreateByTelegramId(1300L);
        UserState state = telegramIdStateService.getOrCreateUserState(1300L);
        Assertions.assertEquals(UserState.DEFAULT, state);
        Assertions.assertTrue(telegramIdStateRepository.existsById(1300L));
    }

    /**
     * Тест получения состояния пользователя с null и некорректным telegramId.
     * <br>
     * Ожидаемое поведение: выбрасывается NullPointerException и IllegalArgumentException соответственно.
     */
    @Test
    public void getUserStateNullTelegramId() {
        NullPointerException ex = Assertions.assertThrows(NullPointerException.class, () ->
                telegramIdStateService.getOrCreateUserState(null)
        );
        Assertions.assertEquals("telegramId не может быть null", ex.getMessage());

        IllegalArgumentException ex2 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                telegramIdStateService.getOrCreateUserState(999999L)
        );
        Assertions.assertEquals(
                "Пользователя с таким telegramId не существует: 999999", ex2.getMessage());
    }

    /**
     * Тест успешного изменения состояния пользователя.
     * <br>
     * Ожидаемое поведение: состояние пользователя обновляется в репозитории.
     */
    @Test
    public void changeUserStateSuccess() {
        userService.getOrCreateByTelegramId(1400L);
        telegramIdStateService.changeUserState(1400L, UserState.AWAITING_TASK_DESCRIPTION);
        UserState state = telegramIdStateService.getOrCreateUserState(1400L);
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, state);
    }

    /**
     * Тест попытки изменения состояния пользователя, который не существует.
     * <br>
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void changeUserStateUserNotFound() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                telegramIdStateService.changeUserState(999998L, UserState.AWAITING_TASK_DESCRIPTION)
        );
    }

    /**
     * Тест изменения состояния пользователя с null telegramId.
     * <br>
     * Ожидаемое поведение: выбрасывается NullPointerException.
     */
    @Test
    public void changeUserStateNullTelegramId() {
        Assertions.assertThrows(NullPointerException.class, () ->
                telegramIdStateService.changeUserState(null, UserState.AWAITING_TASK_DESCRIPTION)
        );
    }

    /**
     * Тест изменения состояния пользователя с null новым состоянием.
     * <br>
     * Ожидаемое поведение: выбрасывается NullPointerException.
     */
    @Test
    public void changeUserStateNullNewState() {
        userService.getOrCreateByTelegramId(1300L);
        Assertions.assertThrows(NullPointerException.class, () ->
                telegramIdStateService.changeUserState(1300L, null)
        );
    }
}
