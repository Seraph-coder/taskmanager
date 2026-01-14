package ru.naujava.taskmanager.entity;

/**
 * Перечисление состояний пользователя.
 *
 * @author Seraph-coder
 * @since 25.11.2025
 */
public enum UserState {
    /**
     * Состояние по умолчанию.
     */
    DEFAULT,
    /**
     * Ожидание описания задачи для добавления.
     */
    AWAITING_TASK_DESCRIPTION,
    /**
     * Ожидание идентификатора задачи для удаления.
     */
    AWAITING_TASK_ID_FOR_DELETION,
    /**
     * Ожидание идентификатора задачи для отметки как выполненной.
     */
    AWAITING_TASK_ID_FOR_COMPLETION
}
