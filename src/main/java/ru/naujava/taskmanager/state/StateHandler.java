package ru.naujava.taskmanager.state;

import ru.naujava.taskmanager.entity.UserState;

/**
 * Интерфейс для обработки состояний пользователя.
 * Определяет состояние, которое обрабатывает handler.
 */
public interface StateHandler {
    /**
     * Возвращает состояние, которое обрабатывает этот handler.
     */
    UserState getState();
}
