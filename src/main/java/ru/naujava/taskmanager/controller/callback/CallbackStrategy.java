package ru.naujava.taskmanager.controller.callback;

import ru.naujava.taskmanager.state.StateTransition;

/**
 * Интерфейс стратегии для обработки callback-команд.
 *
 * @author Seraph-coder
 * @since 02.01.2026
 */
public interface CallbackStrategy {

    /**
     * Возвращает имя callback-команды, которую обрабатывает эта стратегия.
     *
     * @return имя callback-команды
     */
    String getCallbackName();

    /**
     * Обрабатывает callback-команду.
     *
     * @param chatId ID чата пользователя
     * @return переход состояния с ответом
     */
    StateTransition handle(Long chatId);
}
