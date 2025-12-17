package ru.naujava.taskmanager.bot;

import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Лимитер сообщений для предотвращения флуда.
 *
 * @author Seraph-coder
 * @since 14.12.2025
 */
@Component
public class MessageRateLimiter {
    private final Map<Long, Deque<Long>> userMessageTimes = new ConcurrentHashMap<>();
    private final int maxMessagesPerMinute;
    private final long timeWindowMs;

    /**
     * Конструктор по умолчанию для Spring.
     */
    public MessageRateLimiter() {
        this(BotConstants.MAX_MESSAGES_PER_MINUTE, BotConstants.TIME_WINDOW_MS);
    }

    /**
     * Конструктор для тестирования с кастомными параметрами.
     */
    public MessageRateLimiter(int maxMessagesPerMinute, long timeWindowMs) {
        this.maxMessagesPerMinute = maxMessagesPerMinute;
        this.timeWindowMs = timeWindowMs;
    }

    /**
     * Проверяет, превышен ли лимит сообщений для пользователя.
     */
    public boolean isRateLimited(Long chatId) {
        Deque<Long> times = userMessageTimes.computeIfAbsent(chatId, k -> new LinkedList<>());
        synchronized (times) {
            long now = System.currentTimeMillis();
            while (!times.isEmpty() && now - times.peekFirst() > timeWindowMs) {
                times.pollFirst();
            }

            if (times.size() >= maxMessagesPerMinute) {
                if (times.isEmpty()) {
                    userMessageTimes.remove(chatId, times);
                }
                return true;
            }

            times.addLast(now);

            return false;
        }
    }

}
