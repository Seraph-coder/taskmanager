package ru.naujava.taskmanager.bot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Тесты для MessageRateLimiter.
 */
public class MessageRateLimiterTest {
    private MessageRateLimiter rateLimiter;

    @BeforeEach
    public void setup() {
        rateLimiter = new MessageRateLimiter();
    }

    /**
     * Тест, что лимит не превышен для небольшого количества сообщений.
     */
    @Test
    public void testNotRateLimited() {
        Long chatId = 1L;
        for (int i = 0; i < 10; i++) {
            Assertions.assertFalse(rateLimiter.isRateLimited(chatId));
        }
    }

    /**
     * Тест, что лимит превышен после 10 сообщений.
     */
    @Test
    public void testRateLimited() {
        Long chatId = 2L;
        for (int i = 0; i < 11; i++) {
            if (i < 10) {
                Assertions.assertFalse(rateLimiter.isRateLimited(chatId));
            } else {
                Assertions.assertTrue(rateLimiter.isRateLimited(chatId));
            }
        }
    }

    /**
     * Тест сброса ограничения после истечения времени.
     */
    @Test
    public void testRateLimitResetAfterTime() throws InterruptedException {
        rateLimiter = new MessageRateLimiter(2, 5);
        Long chatId = 3L;

        Assertions.assertFalse(rateLimiter.isRateLimited(chatId));
        Assertions.assertFalse(rateLimiter.isRateLimited(chatId));
        Assertions.assertTrue(rateLimiter.isRateLimited(chatId));

        Thread.sleep(5);
        Assertions.assertFalse(rateLimiter.isRateLimited(chatId));
    }

    /**
     * Тест независимости лимитов для разных пользователей.
     */
    @Test
    public void testIndependentLimitsForDifferentUsers() {
        Long chatId1 = 4L;
        Long chatId2 = 5L;
        for (int i = 0; i < 11; i++) {
            if (i < 10) {
                Assertions.assertFalse(rateLimiter.isRateLimited(chatId1));
                Assertions.assertFalse(rateLimiter.isRateLimited(chatId2));
            } else {
                Assertions.assertTrue(rateLimiter.isRateLimited(chatId1));
                Assertions.assertTrue(rateLimiter.isRateLimited(chatId2));
            }
        }
    }
}
