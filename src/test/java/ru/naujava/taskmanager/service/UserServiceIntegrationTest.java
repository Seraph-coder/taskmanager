package ru.naujava.taskmanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
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
     * Проверяет, что при передаче неверного в getOrCreateByTelegramId
     * выбрасывается IllegalArgumentException.
     * <br>
     * Ожидаемое поведение: выбрасывается исключение.
     */
    @Test
    public void getOrCreateByTelegramIdWithInvalidIdThrowsException() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.getOrCreateByTelegramId(-1L)
        );
        Assertions.assertEquals("Telegram ID должен быть положительным числом", ex.getMessage());

        IllegalArgumentException ex2 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.getOrCreateByTelegramId(null)
        );
        Assertions.assertEquals("Telegram ID не может быть null", ex2.getMessage());
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
        User user = new User(3L);

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
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.deleteByTelegramId(-1L)
        );
        Assertions.assertEquals("Telegram ID должен быть положительным числом", ex.getMessage());

        IllegalArgumentException ex2 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.deleteByTelegramId(null)
        );
        Assertions.assertEquals("Telegram ID не может быть null", ex2.getMessage());
    }

    /**
     * Проверяет удаление несуществующего пользователя.
     * <br>
     * Ожидаемое поведение: ничего не происходит.
     */
    @Test
    public void deleteByTelegramIdForNonExistingUserDoesNothing() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.deleteByTelegramId(999L)
        );
        Assertions.assertEquals("Пользователь с таким Telegram ID не найден", ex.getMessage());
    }
}
