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
     * Находит все задачи, принадлежащие пользователю с указанным Telegram ID.
     */
    List<Task> findByUser_TelegramId(Long telegramId);

    /**
     * Находит задачу по её ID и Telegram ID пользователя.
     */
    Optional<Task> findByIdAndUser_TelegramId(Long id, Long telegramId);

    /**
     * Проверяет, существует ли задача с таким описанием у указанного пользователя.
     */
    boolean existsByUser_TelegramIdAndDescription(Long telegramId, String description);
}
