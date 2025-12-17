package ru.naujava.taskmanager.state;

import ru.naujava.taskmanager.entity.UserState;

import java.util.Optional;

/**
 * Интерфейс для обработки состояний пользователя.
 * Определяет состояние, которое обрабатывает handler.
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
public interface StateHandler extends MessageHandler {
    /**
     * Возвращает состояние, которое обрабатывает этот handler.
     */
    UserState getState();

    @Override
    default Optional<UserState> getHandledState() {
        return Optional.of(getState());
    }
}
