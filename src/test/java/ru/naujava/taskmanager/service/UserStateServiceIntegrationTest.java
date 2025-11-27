package ru.naujava.taskmanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.entity.UserStateEnum;
import ru.naujava.taskmanager.repository.UserStateRepository;

import java.util.Optional;

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
     * Тест успешного создания нового состояния пользователя.
     * <br>
     * Ожидаемое поведение: состояние пользователя создается и сохраняется в репозитории.
     */
    @Test
    @Transactional
    public void createUserStateSuccess() {
        userService.getOrCreateByTelegramId(1000L);

        Optional<UserState> created = userStateService.createUserState(1000L);
        Assertions.assertTrue(created.isPresent());
        Assertions.assertTrue(userStateRepository.existsById(1000L));
        Assertions.assertEquals(UserStateEnum.DEFAULT, created.get().getState());
    }

    /**
     * Тест попытки создания состояния пользователя, который уже существует.
     * <br>
     * Ожидаемое поведение: метод возвращает пустой Optional, состояние не создается.
     */
    @Test
    @Transactional
    public void createUserStateAlreadyExists() {
        userService.getOrCreateByTelegramId(1100L);
        userStateService.createUserState(1100L);
        Optional<UserState> second = userStateService.createUserState(1100L);
        Assertions.assertTrue(second.isEmpty());
    }

    /**
     * Тест создания состояния пользователя с несуществующим telegramId.
     * <br>
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void createUserStateUserNotFound() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userStateService.createUserState(999999L)
        );
    }

    /**
     * Тест создания состояния пользователя с null telegramId.
     * <br>
     * Ожидаемое поведение: выбрасывается NullPointerException.
     */
    @Test
    public void createUserStateNullTelegramId() {
        Assertions.assertThrows(NullPointerException.class, () ->
                userStateService.createUserState(null)
        );
    }

    /**
     * Тест успешного получения существующего состояния пользователя.
     * <br>
     * Ожидаемое поведение: возвращается текущее состояние пользователя.
     */
    @Test
    @Transactional
    public void getUserStateSuccess() {
        userService.getOrCreateByTelegramId(1200L);
        userStateService.createUserState(1200L);
        userStateService.changeUserState(1200L, UserStateEnum.AWAITING_TASK_DESCRIPTION);
        UserStateEnum state = userStateService.getUserState(1200L);
        Assertions.assertEquals(UserStateEnum.AWAITING_TASK_DESCRIPTION, state);
    }

    /**
     * Тест получения состояния пользователя, когда состояние не существует.
     * <br>
     * Ожидаемое поведение: создается новое состояние пользователя с состоянием DEFAULT.
     */
    @Test
    @Transactional
    public void getUserStateCreatesNewIfNotExists() {
        userService.getOrCreateByTelegramId(1300L);
        UserStateEnum state = userStateService.getUserState(1300L);
        Assertions.assertEquals(UserStateEnum.DEFAULT, state);
        Assertions.assertTrue(userStateRepository.existsById(1300L));
    }

    /**
     * Тест получения состояния пользователя с null telegramId.
     * <br>
     * Ожидаемое поведение: выбрасывается NullPointerException.
     */
    @Test
    public void getUserStateNullTelegramId() {
        Assertions.assertThrows(NullPointerException.class, () ->
                userStateService.getUserState(null)
        );
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
        userStateService.createUserState(1400L);
        userStateService.changeUserState(1400L, UserStateEnum.AWAITING_TASK_DESCRIPTION);
        UserStateEnum state = userStateService.getUserState(1400L);
        Assertions.assertEquals(UserStateEnum.AWAITING_TASK_DESCRIPTION, state);
    }

    /**
     * Тест попытки изменения состояния пользователя, который не существует.
     * <br>
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void changeUserStateUserNotFound() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userStateService.changeUserState(999998L, UserStateEnum.AWAITING_TASK_DESCRIPTION)
        );
    }

    /**
     * Тест попытки изменения состояния пользователя, состояние которого не существует.
     * <br>
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void changeUserStateStateNotFound() {
        userService.getOrCreateByTelegramId(1500L);
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userStateService.changeUserState(1500L, UserStateEnum.AWAITING_TASK_DESCRIPTION)
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
                userStateService.changeUserState(null, UserStateEnum.AWAITING_TASK_DESCRIPTION)
        );
    }

    /**
     * Тест сброса состояния пользователя.
     * <br>
     * Ожидаемое поведение: состояние пользователя сбрасывается в DEFAULT.
     */
    @Test
    @Transactional
    public void resetUserStateSuccess() {
        userService.getOrCreateByTelegramId(1600L);
        userStateService.createUserState(1600L);
        userStateService.changeUserState(1600L, UserStateEnum.AWAITING_TASK_DESCRIPTION);
        boolean ok = userStateService.resetUserState(1600L);
        Assertions.assertTrue(ok);
        Assertions.assertEquals(UserStateEnum.DEFAULT, userStateService.getUserState(1600L));
    }

    /**
     * Тест сброса состояния пользователя, который не существует.
     * <br>
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void resetUserStateUserNotFound() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userStateService.resetUserState(999997L)
        );
    }

    /**
     * Тест сброса состояния пользователя с null telegramId.
     * <br>
     * Ожидаемое поведение: выбрасывается NullPointerException.
     */
    @Test
    public void resetUserStateNullTelegramId() {
        Assertions.assertThrows(NullPointerException.class, () ->
                userStateService.resetUserState(null)
        );
    }
}
