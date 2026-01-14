package ru.naujava.taskmanager.state;

import ru.naujava.taskmanager.entity.UserState;

/**
 * Интерфейс для обработки состояний.
 *
 * @author Seraph-coder
 * @since 13.01.2026
 */
public interface StateHandler {
    /**
     * Возвращает состояние, которое обрабатывается этим обработчиком.
     */
    UserState getHandledState();

    /**
     * Обрабатывает сообщение для заданного состояния.
     */
    StateTransition handle(Long chatId, String text);
}
