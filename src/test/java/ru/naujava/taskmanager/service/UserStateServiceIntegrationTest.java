package ru.naujava.taskmanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.entity.UserStateEnum;
import ru.naujava.taskmanager.repository.UserStateRepository;

/**
 * Тесты для сервиса управления состояниями пользователей {@link UserStateService}.
 *
 * @author Seraph-coder
 * @since 26.11.2025
 */
@SpringBootTest
@ActiveProfiles("test")
public class UserStateServiceIntegrationTest {
    @Autowired
    private UserStateRepository userStateRepository;

    @Autowired
    private UserStateService userStateService;

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
        UserStateEnum state = userStateService.getOrCreateUserState(1300L);
        Assertions.assertEquals(UserStateEnum.DEFAULT, state);
        Assertions.assertTrue(userStateRepository.existsById(1300L));
        userStateService.getOrCreateUserState(1300L);
    }

    /**
     * Тест получения состояния пользователя с null и некорректным telegramId.
     * <br>
     * Ожидаемое поведение: выбрасывается NullPointerException и IllegalArgumentException соответственно.
     */
    @Test
    public void getUserStateNullTelegramId() {
        NullPointerException ex = Assertions.assertThrows(NullPointerException.class, () ->
                userStateService.getOrCreateUserState(null)
        );
        Assertions.assertEquals("telegramId не может быть null", ex.getMessage());

        IllegalArgumentException ex2 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                userStateService.getOrCreateUserState(999999L)
        );
        Assertions.assertEquals(
                "Пользователя с таким telegramId не существует: 999999", ex2.getMessage());
    }

    /**
     * Изменяет состояние пользователя и проверяет корректность обновления.
     * <br>
     * Ожидаемое поведение: состояние пользователя успешно обновляется в репозитории.
     */
    @Test
    @Transactional
    public void changeUserStateSuccess() {
        userService.getOrCreateByTelegramId(1400L);
        userStateService.getOrCreateUserState(1400L);
        userStateService.changeUserState(1400L, UserStateEnum.AWAITING_TASK_DESCRIPTION);
        UserStateEnum state = userStateService.getOrCreateUserState(1400L);
        Assertions.assertEquals(UserStateEnum.AWAITING_TASK_DESCRIPTION, state);
    }

    /**
     * Тест попытки изменения состояния пользователя, который не существует.
     * <br>
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void changeUserStateUserNotFound() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                userStateService.changeUserState(999998L, UserStateEnum.AWAITING_TASK_DESCRIPTION)
        );
        Assertions.assertEquals(
                "Пользователя с таким telegramId не существует: 999998", ex.getMessage());
    }

    /**
     * Тест изменения состояния пользователя с null и некорректным telegramId.
     * <br>
     * Ожидаемое поведение: выбрасывается NullPointerException.
     */
    @Test
    public void changeUserStateNullTelegramId() {
        NullPointerException ex = Assertions.assertThrows(NullPointerException.class, () ->
                userStateService.changeUserState(null, UserStateEnum.AWAITING_TASK_DESCRIPTION)
        );
        Assertions.assertEquals("telegramId не может быть null", ex.getMessage());

        IllegalArgumentException ex2 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                userStateService.changeUserState(999997L, UserStateEnum.AWAITING_TASK_DESCRIPTION)
        );
        Assertions.assertEquals(
                "Пользователя с таким telegramId не существует: 999997", ex2.getMessage());
    }

    /**
     * Тест изменения состояния пользователя с null новым состоянием.
     * <br>
     * Ожидаемое поведение: выбрасывается NullPointerException.
     */
    @Test
    public void changeUserStateNullNewState() {
        userService.getOrCreateByTelegramId(1300L);
        NullPointerException ex = Assertions.assertThrows(NullPointerException.class, () ->
                userStateService.changeUserState(1300L, null)
        );
        Assertions.assertEquals("newState не может быть null", ex.getMessage());
    }
}
