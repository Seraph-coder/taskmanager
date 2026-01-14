package ru.naujava.taskmanager.controller.callback;

import ru.naujava.taskmanager.state.StateTransition;

/**
 * Интерфейс для обработки callback-запросов.
 *
 * @author Seraph-coder
 * @since 13.01.2026
 */
public interface CallbackProcessor {
    /**
     * Обрабатывает callback-запрос.
     *
     * @param callbackData данные callback-запроса
     * @param chatId       ID чата пользователя
     * @return результат обработки callback-запроса
     */
    StateTransition processCallback(String callbackData, Long chatId);

    boolean isCallback(String callbackData);
}
