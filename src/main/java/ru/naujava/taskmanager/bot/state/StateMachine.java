package ru.naujava.taskmanager.bot.state;

import ru.naujava.taskmanager.entity.UserStateEnum;

/**
 * Базовый интерфейс стейтмашины для управления состоянием пользователя.
 *
 * @author Seraph-coder
 * @since 25.11.2025
 */
public interface StateMachine {
    /**
     * Возвращает текущее состояние пользователя по его chatId.
     */
    UserStateEnum getState(Long chatId);

    /**
     * Устанавливает новое состояние пользователя по его chatId.
     */
    void setState(Long chatId, UserStateEnum state);

    /**
     * Сбрасывает состояние пользователя по его chatId в состояние по умолчанию.
     */
    void reset(Long chatId);
}

