package ru.naujava.taskmanager.state;

import ru.naujava.taskmanager.entity.UserState;

import java.util.Optional;

/**
 * Интерфейс для обработки сообщений в зависимости от состояния.
 * Объединяет обработку команд и состояний.
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
public interface MessageHandler {

    /**
     * Возвращает состояние, которое обрабатывает этот хендлер, если применимо.
     */
    default Optional<UserState> getHandledState() {
        return Optional.empty();
    }

    /**
     * Обрабатывает сообщение и возвращает переход состояния.
     */
    StateTransition handle(Long chatId, String text);
}
