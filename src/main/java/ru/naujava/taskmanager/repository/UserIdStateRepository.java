package ru.naujava.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.naujava.taskmanager.entity.UserIdState;

/**
 * Репозиторий для управления состояниями пользователей.
 *
 * @author Seraph-coder
 * @since 25.11.2025
 */
public interface UserIdStateRepository extends JpaRepository<UserIdState, Long> {
}
