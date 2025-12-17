package ru.naujava.taskmanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.naujava.taskmanager.entity.User;

import java.util.Optional;

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
        Optional<User> userOpt = userService.findByTelegramId(1L);
        Assertions.assertTrue(userOpt.isPresent());
        Assertions.assertEquals(1L, userOpt.get().getTelegramId());
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
}
