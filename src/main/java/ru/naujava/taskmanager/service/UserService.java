package ru.naujava.taskmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.naujava.taskmanager.entity.User;
import ru.naujava.taskmanager.repository.UserRepository;

import java.util.Optional;

/**
 * Сервис для управления пользователями.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    /**
     * Конструктор сервиса пользователей.
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Находит пользователя по User ID.
     */
    public Optional<User> findByUserId(Long userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * Возвращает существующего пользователя по User ID или создает
     * нового, если пользователь не найден.
     *
     * @throws IllegalArgumentException если userId null или не положительное число
     */
    public User getOrCreateByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID не может быть null");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID должен быть положительным числом");
        }
        return userRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUserId(userId);
                    User saved = userRepository.save(newUser);
                    log.debug("Создан новый пользователь с userId: {}", userId);
                    return saved;
                });
    }
}
