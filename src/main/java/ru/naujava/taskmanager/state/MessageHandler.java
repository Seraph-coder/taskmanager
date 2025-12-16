package ru.naujava.taskmanager.state;

import ru.naujava.taskmanager.entity.UserState;

/**
 * Интерфейс для обработки сообщений в зависимости от состояния.
 * Объединяет обработку команд и состояний.
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
public interface MessageHandler {

    /**
     * Возвращает состояние, которое обрабатывает этот хендлер.
     */
    UserState getState();

    /**
     * Обрабатывает сообщение и возвращает переход состояния.
     */
    StateTransition handle(Long chatId, String text);
}
