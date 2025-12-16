package ru.naujava.taskmanager.controller;

/**
 * Действия, которые могут быть выполнены после команды.
 *
 * @author Seraph-coder
 * @since 11.12.2025
 */
public enum Action {
    /**
     * Нет действия.
     */
    NONE,
    /**
     * Отправить меню.
     */
    SEND_MENU,
    /**
     * Отменить добавление задачи.
     */
    CANCEL_ADD_TASK,
    /**
     * Отменить удаление задачи.
     */
    CANCEL_DELETE_TASK,
    /**
     * Сброс состояния пользователя.
     */
    RESET_STATE,
    /**
     * Установка нового состояния пользователя.
     */
    SET_STATE,
    /**
     * Отправить кнопку отмены.
     */
    SEND_CANCEL,
}