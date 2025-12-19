package ru.naujava.taskmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.entity.User;
import ru.naujava.taskmanager.repository.TaskRepository;

import java.util.List;
import java.util.Objects;

/**
 * Сервис для управления задачами.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
@Service
@Transactional
public class TaskService {
    private final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final UserService userService;

    /**
     * Конструктор сервиса задач.
     */
    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    /**
     * Находит все задачи пользователя по его Telegram ID и возвращает их в отсортированном по ID порядке.
     */
    public List<Task> findAllTasksByTelegramId(Long telegramId) {
        Objects.requireNonNull(telegramId, "telegramId не должен быть null");
        return taskRepository.findByUser_TelegramIdOrderByIdAsc(telegramId);
    }

    /**
     * Находит все выполненные задачи пользователя по его Telegram ID и возвращает их в отсортированном по ID порядке.
     */
    public List<Task> getCompletedTasks(Long telegramId) {
        Objects.requireNonNull(telegramId, "telegramId не должен быть null");
        return taskRepository.findByUser_TelegramIdAndDoneTrueOrderByIdAsc(telegramId);
    }

    /**
     * Находит все невыполненные задачи пользователя по его Telegram ID
     * и возвращает их в отсортированном по ID порядке.
     */
    public List<Task> getUncompletedTasks(Long telegramId) {
        Objects.requireNonNull(telegramId, "telegramId не должен быть null");
        return taskRepository.findByUser_TelegramIdAndDoneFalseOrderByIdAsc(telegramId);
    }


    /**
     * Создает задачу, связывая её с пользователем по Telegram ID.
     *
     * @throws IllegalArgumentException если задача с таким описанием уже существует
     *                                  или если входные параметры некорректны
     */
    public Task createTask(String taskDescription, Long telegramId) {
        Objects.requireNonNull(taskDescription, "taskDescription не должен быть null");
        Objects.requireNonNull(telegramId, "telegramId не должен быть null");
        if (taskDescription.isEmpty()) {
            throw new IllegalArgumentException("taskDescription не должен быть пустым");
        }

        boolean exists = taskRepository.existsByUser_TelegramIdAndDescription(telegramId, taskDescription);
        if (exists) {
            throw new IllegalArgumentException("Задача с описанием '" +
                    taskDescription + "' уже существует");
        }

        User user = userService.getOrCreateByTelegramId(telegramId);

        Task task = new Task();
        task.setUser(user);
        task.setDescription(taskDescription);
        Task savedTask = taskRepository.save(task);
        log.info("Создана задача '{}' для пользователя с telegramId: {}", taskDescription, telegramId);
        return savedTask;
    }

    /**
     * Удаляет задачу по её ID и Telegram ID пользователя.
     *
     * @throws IllegalArgumentException если задача не найдена
     */
    public Task deleteTaskByIdAndTelegramId(Long taskId, Long telegramId) {
        Objects.requireNonNull(taskId, "taskId не должен быть null");
        Objects.requireNonNull(telegramId, "telegramId не должен быть null");

        Task task = taskRepository.findByIdAndUser_TelegramId(taskId, telegramId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));
        taskRepository.delete(task);
        log.info("Удалена задача с id: {} для пользователя с telegramId: {}", taskId, telegramId);
        return task;
    }

    /**
     * Удаляет задачу по её номеру в списке невыполненных задач пользователя.
     *
     * @throws IllegalArgumentException если задача не найдена или индекс некорректен
     */
    public Task deleteTaskByIndexAndTelegramId(int taskIndex, Long telegramId) {
        Objects.requireNonNull(telegramId, "telegramId не должен быть null");
        if (taskIndex < 1) {
            throw new IllegalArgumentException("Номер задачи должен быть положительным");
        }

        List<Task> tasks = getUncompletedTasks(telegramId);
        if (taskIndex > tasks.size()) {
            throw new IllegalArgumentException("Ошибка: Задача " + taskIndex + " не найдена");
        }

        Task toDelete = tasks.get(taskIndex - 1);
        taskRepository.delete(toDelete);
        log.info("Удалена задача '{}' (индекс {}) для пользователя с telegramId: {}",
                toDelete.getDescription(), taskIndex, telegramId);
        return toDelete;
    }

    /**
     * Возвращает отформатированный список невыполненных задач пользователя.
     */
    public String formatUncompletedTaskAsString(Long telegramId) {
        List<Task> tasks = getUncompletedTasks(telegramId);
        return formatTaskList(tasks);
    }

    /**
     * Возвращает отформатированный список выполненных задач пользователя.
     */
    public String formatCompletedTaskAsString(Long telegramId) {
        List<Task> tasks = getCompletedTasks(telegramId);
        return formatTaskList(tasks);
    }

    /**
     * Форматирует список задач в строку с нумерацией.
     */
    private String formatTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return BotConstants.MSG_TASKS_EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            String desc = Objects.toString(tasks.get(i).getDescription(), "");
            sb.append(i + 1).append(") ").append(desc);
            if (i < tasks.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Помечает задачу как выполненную по её номеру в списке невыполненных задач пользователя.
     *
     * @throws IllegalArgumentException если задача не найдена или индекс некорректен
     */
    public Task markTaskCompletedByIndexAndTelegramId(int taskIndex, Long telegramId) {
        Objects.requireNonNull(telegramId, "telegramId не должен быть null");
        if (taskIndex < 1) {
            throw new IllegalArgumentException("Номер задачи должен быть положительным");
        }

        List<Task> tasks = getUncompletedTasks(telegramId);
        if (taskIndex > tasks.size()) {
            throw new IllegalArgumentException("Задача с номером " + taskIndex + " не найдена");
        }

        Task toCompleted = tasks.get(taskIndex - 1);
        toCompleted.setDone(true);
        taskRepository.save(toCompleted);
        log.info("Помечена как выполненная задача '{}' (индекс {}) для пользователя с telegramId: {}",
                toCompleted.getDescription(), taskIndex, telegramId);
        return toCompleted;
    }
}
