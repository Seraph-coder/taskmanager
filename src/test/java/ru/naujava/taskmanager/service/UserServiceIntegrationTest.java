package ru.naujava.taskmanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.naujava.taskmanager.builder.UserTestBuilder;
import ru.naujava.taskmanager.entity.User;

/**
 * Тесты для сервиса пользователей {@link UserService}.
 *
 * @author Seraph-coder
 * @since 15.11.2025
 */
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    /**
     * Проверяет создание и поиск пользователя по Telegram ID.
     * <br>
     * Ожидаемое поведение: создает и возвращает существующего пользователя.
     */
    @Test
    public void getOrCreateByTelegramIdAndFindByTelegramId() {
        userService.getOrCreateByTelegramId(1L);
        User foundUser = userService.findByTelegramId(1L).orElseThrow();
        Assertions.assertEquals(1L, foundUser.getTelegramId());
    }

    /**
     * Проверяет, что при повторном вызове getOrCreateByTelegramId
     * возвращается тот же пользователь.
     * <br>
     * Ожидаемое поведение: возвращает существующего пользователя без создания нового.
     */
    @Test
    public void getOrCreateByTelegramIdReturnsExistingUser() {
        User firstCallUser = userService.getOrCreateByTelegramId(2L);
        User secondCallUser = userService.getOrCreateByTelegramId(2L);
        Assertions.assertEquals(firstCallUser.getId(), secondCallUser.getId());
    }

    /**
     * Проверяет, что при передаче неверного в getOrCreateByTelegramId
     * выбрасывается IllegalArgumentException.
     * <br>
     * Ожидаемое поведение: выбрасывается исключение.
     */
    @Test
    public void getOrCreateByTelegramIdWithInvalidIdThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                        userService.getOrCreateByTelegramId(-1L),
                "Telegram ID должен быть положительным числом"
        );
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.getOrCreateByTelegramId(null), "Telegram ID не может быть null"
        );
    }

    /**
     * Проверяет поиск пользователя по-некорректному Telegram ID.
     * <br>
     * Ожидаемое поведение: возвращается пустой Optional.
     */
    @Test
    public void findByTelegramIdWithInvalidIdReturnsEmpty() {
        Assertions.assertTrue(userService.findByTelegramId(-1L).isEmpty());
    }

    /**
     * Проверяет удаление пользователя по Telegram ID.
     * <br>
     * Ожидаемое поведение: пользователь удаляется и не находится при последующем поиске.
     */
    @Test
    public void deleteByTelegramId() {
        User user = new UserTestBuilder().withId(3L).withTelegramId(3L).build();
        userService.getOrCreateByTelegramId(user.getTelegramId());
        userService.deleteByTelegramId(user.getTelegramId());
        Assertions.assertTrue(userService.findByTelegramId(user.getTelegramId()).isEmpty());
    }

    /**
     * Проверяет удаление пользователя с невалидным Telegram ID.
     * <br>
     * Ожидаемое поведение: выбрасывается IllegalArgumentException.
     */
    @Test
    public void deleteByTelegramIdWithInvalidIdThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                        userService.deleteByTelegramId(-1L),
                "Telegram ID должен быть положительным числом"
        );
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.deleteByTelegramId(null), "Telegram ID не может быть null"
        );
    }

    /**
     * Проверяет удаление несуществующего пользователя.
     * <br>
     * Ожидаемое поведение: ничего не происходит.
     */
    @Test
    public void deleteByTelegramIdForNonExistingUserDoesNothing() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                        userService.deleteByTelegramId(999L),
                "Пользователь с таким Telegram ID не найден"
        );
    }
}
