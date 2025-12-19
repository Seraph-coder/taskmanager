package ru.naujava.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.naujava.taskmanager.entity.Task;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления задачами.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
    /**
     * Находит все задачи пользователя и сортирует их по id по возрастанию.
     */
    List<Task> findByUser_TelegramIdOrderByIdAsc(Long telegramId);

    /**
     * Находит задачу по её ID и Telegram ID пользователя.
     */
    Optional<Task> findByIdAndUser_TelegramId(Long id, Long telegramId);

    /**
     * Проверяет, существует ли задача с таким описанием у указанного пользователя.
     */
    boolean existsByUser_TelegramIdAndDescription(Long telegramId, String description);

    /**
     * Находит все невыполненные задачи пользователя и сортирует их по id по возрастанию.
     */
    List<Task> findByUser_TelegramIdAndDoneFalseOrderByIdAsc(Long telegramId);

    /**
     * Находит все выполненные задачи пользователя и сортирует их по id по возрастанию.
     */
    List<Task> findByUser_TelegramIdAndDoneTrueOrderByIdAsc(Long telegramId);
}
