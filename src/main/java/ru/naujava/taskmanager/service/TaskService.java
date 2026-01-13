package ru.naujava.taskmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
     * Находит все задачи пользователя по его User ID и возвращает их в отсортированном по ID порядке.
     */
    public List<Task> findAllTasksByUserId(Long userId) {
        Objects.requireNonNull(userId, "userId не должен быть null");
        return taskRepository.findByUser_UserIdOrderByIdAsc(userId);
    }

    /**
     * Находит все выполненные задачи пользователя по его User ID и возвращает их в отсортированном по ID порядке.
     */
    public List<Task> getCompletedTasks(Long userId) {
        Objects.requireNonNull(userId, "userId не должен быть null");
        return taskRepository.findByUser_UserIdAndDoneTrueOrderByIdAsc(userId);
    }

    /**
     * Находит все невыполненные задачи пользователя по его User ID
     * и возвращает их в отсортированном по ID порядке.
     */
    public List<Task> getUncompletedTasks(Long userId) {
        Objects.requireNonNull(userId, "userId не должен быть null");
        return taskRepository.findByUser_UserIdAndDoneFalseOrderByIdAsc(userId);
    }


    /**
     * Создает задачу, связывая её с пользователем по User ID.
     *
     * @throws IllegalArgumentException если задача с таким описанием уже существует
     *                                  или если входные параметры некорректны
     */
    public Task createTask(String taskDescription, Long userId) {
        Objects.requireNonNull(userId, "userId не должен быть null");
        Objects.requireNonNull(taskDescription, "taskDescription не должен быть null");
        if (taskDescription.isEmpty()) {
            throw new IllegalArgumentException("Описание задачи не может быть пустым");
        }

        boolean exists = taskRepository.existsByUser_UserIdAndDescription(userId, taskDescription);
        if (exists) {
            throw new IllegalArgumentException("Задача с описанием '" + taskDescription + "' уже существует");
        }

        User user = userService.getOrCreateByUserId(userId);
        Task task = new Task(taskDescription, user);
        taskRepository.save(task);

        log.debug("Создана задача '{}' для пользователя с userId: {}", taskDescription, userId);
        return task;
    }

    /**
     * Удаляет задачу по её ID и User ID пользователя.
     *
     * @throws IllegalArgumentException если задача не найдена
     */
    public Task deleteTaskByIdAndUserId(Long taskId, Long userId) {
        Objects.requireNonNull(userId, "userId не должен быть null");
        Objects.requireNonNull(taskId, "taskId не должен быть null");

        Task task = taskRepository.findByIdAndUser_UserId(taskId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));

        taskRepository.delete(task);
        log.debug("Удалена задача с id: {} для пользователя с userId: {}", taskId, userId);
        return task;
    }

    /**
     * Удаляет задачу по её номеру в списке невыполненных задач пользователя.
     *
     * @throws IllegalArgumentException если задача не найдена или индекс некорректен
     */
    public Task deleteTaskByIndexAndUserId(int taskIndex, Long userId) {
        Objects.requireNonNull(userId, "userId не должен быть null");

        if (taskIndex < 1) {
            throw new IllegalArgumentException("Номер задачи должен быть положительным");
        }

        List<Task> tasks = getUncompletedTasks(userId);
        if (taskIndex > tasks.size()) {
            throw new IllegalArgumentException("Ошибка: Задача с номером " + taskIndex + " не найдена");
        }

        Task task = tasks.get(taskIndex - 1);
        taskRepository.delete(task);
        log.debug("Удалена задача с номером: {} для пользователя с userId: {}", taskIndex, userId);
        return task;
    }

    /**
     * Удаляет задачу по её номеру из объединенного списка задач
     * (сначала выполненные, потом невыполненные).
     *
     * @throws IllegalArgumentException если задача не найдена или индекс некорректен
     */
    public Task deleteTaskByIndexFromCombinedList(int taskIndex, Long userId) {
        Objects.requireNonNull(userId, "userId не должен быть null");

        if (taskIndex < 1) {
            throw new IllegalArgumentException("Номер задачи должен быть положительным");
        }

        List<Task> completedTasks = getCompletedTasks(userId);
        List<Task> uncompletedTasks = getUncompletedTasks(userId);
        int totalSize = completedTasks.size() + uncompletedTasks.size();

        if (taskIndex > totalSize) {
            throw new IllegalArgumentException("Задача с номером " + taskIndex + " не найдена");
        }

        Task toDelete;
        if (taskIndex <= completedTasks.size()) {
            toDelete = completedTasks.get(taskIndex - 1);
        } else {
            toDelete = uncompletedTasks.get(taskIndex - completedTasks.size() - 1);
        }

        taskRepository.delete(toDelete);
        log.debug("Удалена задача с номером {} из объединенного списка задач для пользователя с userId: {}",
                taskIndex, userId);
        return toDelete;
    }

    /**
     * Возвращает отформатированный список невыполненных задач пользователя.
     * Используется для команды /todo.
     */
    public String formatTaskList(Long userId) {
        List<Task> tasks = getUncompletedTasks(userId);
        return formatTaskListInternal(tasks);
    }

    /**
     * Возвращает отформатированный список задач для удаления:
     * сначала выполненные, потом невыполненные.
     * Показывает заголовки блоков только если есть задачи в них.
     */
    public String formatTaskListForDeletion(Long userId) {
        List<Task> completedTasks = getCompletedTasks(userId);
        List<Task> uncompletedTasks = getUncompletedTasks(userId);

        if (completedTasks.isEmpty() && uncompletedTasks.isEmpty()) {
            return BotConstants.MSG_TASKS_EMPTY;
        }

        StringBuilder sb = new StringBuilder();
        int index = 1;

        // Сначала выполненные (если есть)
        if (!completedTasks.isEmpty()) {
            sb.append("Выполненные задачи\n");
            for (int i = 0; i < completedTasks.size(); i++) {
                sb.append(index++).append(") ").append(completedTasks.get(i).getDescription()).append(" ✓");
                if (i < completedTasks.size() - 1) {
                    sb.append("\n");
                }
            }

            // Добавляем пустую строку перед следующим блоком, если есть невыполненные
            if (!uncompletedTasks.isEmpty()) {
                sb.append("\n\n");
            }
        }

        // Потом невыполненные (если есть)
        if (!uncompletedTasks.isEmpty()) {
            sb.append("Невыполненные задачи\n");
            for (int i = 0; i < uncompletedTasks.size(); i++) {
                sb.append(index++).append(") ").append(uncompletedTasks.get(i).getDescription());
                if (i < uncompletedTasks.size() - 1) {
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }

    /**
     * Возвращает отформатированный список невыполненных задач пользователя.
     */
    public String formatUncompletedTaskAsString(Long userId) {
        List<Task> tasks = getUncompletedTasks(userId);
        return formatTaskListInternal(tasks);
    }

    /**
     * Возвращает отформатированный список выполненных задач пользователя.
     */
    public String formatCompletedTaskAsString(Long userId) {
        List<Task> tasks = getCompletedTasks(userId);
        if (tasks.isEmpty()) {
            return BotConstants.MSG_TASKS_EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(") ").append(tasks.get(i).getDescription()).append(" ✓");
            if (i < tasks.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Форматирует список задач в строку с нумерацией.
     */
    private String formatTaskListInternal(List<Task> tasks) {
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
    public Task markTaskCompletedByIndexAndUserId(int taskIndex, Long userId) {
        Objects.requireNonNull(userId, "userId не должен быть null");
        if (taskIndex < 1) {
            throw new IllegalArgumentException("Номер задачи должен быть положительным");
        }

        List<Task> tasks = getUncompletedTasks(userId);
        if (taskIndex > tasks.size()) {
            throw new IllegalArgumentException("Задача с номером " + taskIndex + " не найдена");
        }

        Task toCompleted = tasks.get(taskIndex - 1);
        toCompleted.setDone(true);
        taskRepository.save(toCompleted);
        log.debug("Помечена как выполненная задача '{}' (индекс {}) для пользователя с UserId: {}",
                toCompleted.getDescription(), taskIndex, userId);
        return toCompleted;
    }
}
