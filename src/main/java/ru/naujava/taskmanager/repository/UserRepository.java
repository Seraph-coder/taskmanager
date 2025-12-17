package ru.naujava.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.naujava.taskmanager.entity.User;

import java.util.Optional;

/**
 * Репозиторий для управления пользователями.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Находит пользователя по его Telegram ID.
     */
    Optional<User> findByTelegramId(Long telegramId);
}
