package ru.naujava.taskmanager.bot;

/**
 * Константы для бота.
 * Добавил константы для callback данных, текстов кнопок и сообщений,
 * чтобы было проще управлять ими в одном месте.
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
public final class BotConstants {
    // Callback data
    public static final String CALLBACK_ADD = "ADD";
    public static final String CALLBACK_DELETE = "DELETE";
    public static final String CALLBACK_LIST = "LIST";
    public static final String CALLBACK_DONE = "DONE";
    public static final String CALLBACK_SHOWDONE = "SHOWDONE";
    public static final String CALLBACK_CANCEL = "CANCEL";

    // Текст кнопок
    public static final String BUTTON_ADD_TASK = "Добавить задачу";
    public static final String BUTTON_DELETE_TASK = "Удалить задачу";
    public static final String BUTTON_LIST_TASKS = "Список задач";
    public static final String BUTTON_MARK_DONE = "Отметить выполненной";
    public static final String BUTTON_SHOW_COMPLETED = "Выполненные задачи";
    public static final String BUTTON_CANCEL = "Отменить";

    // Сообщения
    public static final String MSG_UNKNOWN_COMMAND = "Неизвестная команда. Введите /help для списка команд";
    public static final String MSG_WELCOME = "Добро пожаловать в Task Manager Bot! Введите /help для списка команд";
    public static final String MSG_EMPTY_CALLBACK = "Пустые данные обратного вызова";
    public static final String MSG_TASKS_EMPTY = "Список задач пуст";
    public static final String MSG_ENTER_TASK_DESCRIPTION = "Введите описание задачи";
    public static final String MSG_ENTER_TASK_NUMBER_DELETE = "Введите номер задачи для удаления";
    public static final String MSG_ENTER_TASK_NUMBER_COMPLETE = "Введите номер задачи для отметки выполненной";
    public static final String MSG_UNKNOWN_USER = "Неизвестный пользователь";
    public static final String MSG_ACTION_CANCELLED = "Действие отменено";

    // Ограничения на частоту сообщений
    public static final int MAX_MESSAGES_PER_MINUTE = 10;
    public static final long TIME_WINDOW_MS = 60000;


    /**
     * Приватный конструктор для предотвращения создания экземпляров класса.
     */
    private BotConstants() {
    }
}
