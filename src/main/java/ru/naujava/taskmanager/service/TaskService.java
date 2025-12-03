package ru.naujava.taskmanager.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final TaskRepository taskRepository;
    private final UserService userService;

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
     * Создает задачу, связывая её с пользователем по Telegram ID.
     * @throws IllegalArgumentException если задача с таким описанием уже существует
     * или если входные параметры некорректны
     */
    public Task createTask(String taskDescription, Long telegramId) {
        Objects.requireNonNull(taskDescription, "taskDescription не должен быть null");
        Objects.requireNonNull(telegramId, "telegramId не должен быть null");
        if (taskDescription.isEmpty()) {
            throw new IllegalArgumentException("taskDescription не должен быть пустым");
        }

        boolean exists = taskRepository.existsByUser_TelegramIdAndDescription(telegramId, taskDescription);
        if (exists) {
            throw new IllegalArgumentException("Задача с описанием '" + taskDescription + "' уже существует");
        }

        User user = userService.getOrCreateByTelegramId(telegramId);

        Task task = new Task();
        task.setUser(user);
        task.setDescription(taskDescription);
        return taskRepository.save(task);
    }

    /**
     * Удаляет задачу по её ID и Telegram ID пользователя.
     * @throws IllegalArgumentException если задача не найдена
     */
    public Task deleteTaskByIdAndTelegramId(Long taskId, Long telegramId) {
        Objects.requireNonNull(taskId, "taskId не должен быть null");
        Objects.requireNonNull(telegramId, "telegramId не должен быть null");

        Task task = taskRepository.findByIdAndUser_TelegramId(taskId, telegramId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));
        taskRepository.delete(task);
        return task;
    }
}
