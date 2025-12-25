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
     * Отменить добавление задачи.
     */
    CANCEL_ADD_TASK,
    /**
     * Отменить удаление задачи.
     */
    CANCEL_DELETE_TASK,
    /**
     * Отправить кнопку отмены.
     */
    SEND_CANCEL
}