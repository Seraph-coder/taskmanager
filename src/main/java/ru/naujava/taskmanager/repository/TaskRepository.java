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
    List<Task> findByUser_UserIdOrderByIdAsc(Long userId);

    /**
     * Находит задачу по её ID и User ID пользователя.
     */
    Optional<Task> findByIdAndUser_UserId(Long id, Long userId);

    /**
     * Проверяет, существует ли задача с таким описанием у указанного пользователя.
     */
    boolean existsByUser_UserIdAndDescription(Long userId, String description);

    /**
     * Находит все невыполненные задачи пользователя и сортирует их по id по возрастанию.
     */
    List<Task> findByUser_UserIdAndDoneFalseOrderByIdAsc(Long userId);

    /**
     * Находит все выполненные задачи пользователя и сортирует их по id по возрастанию.
     */
    List<Task> findByUser_UserIdAndDoneTrueOrderByIdAsc(Long userId);
}
