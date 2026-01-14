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
     * Проверяет создание и поиск пользователя по User ID.
     * <br>
     * Ожидаемое поведение: создает и возвращает существующего пользователя.
     */
    @Test
    public void getOrCreateByUserIdAndFindByUserId() {
        userService.getOrCreateByUserId(1L);
        Optional<User> userOpt = userService.findByUserId(1L);
        Assertions.assertTrue(userOpt.isPresent());
        Assertions.assertEquals(1L, userOpt.get().getUserId());
    }

    /**
     * Проверяет, что при передаче неверного в getOrCreateByUserId
     * выбрасывается IllegalArgumentException.
     * <br>
     * Ожидаемое поведение: выбрасывается исключение.
     */
    @Test
    public void getOrCreateByUserIdWithInvalidIdThrowsException() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.getOrCreateByUserId(-1L)
        );
        Assertions.assertEquals("User ID должен быть положительным числом", ex.getMessage());

        IllegalArgumentException ex2 = Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.getOrCreateByUserId(null)
        );
        Assertions.assertEquals("User ID не может быть null", ex2.getMessage());
    }

    /**
     * Проверяет поиск пользователя по-некорректному User ID.
     * <br>
     * Ожидаемое поведение: возвращается пустой Optional.
     */
    @Test
    public void findByUserIdWithInvalidIdReturnsEmpty() {
        Assertions.assertTrue(userService.findByUserId(-1L).isEmpty());
        Assertions.assertTrue(userService.findByUserId(null).isEmpty());
    }
}
