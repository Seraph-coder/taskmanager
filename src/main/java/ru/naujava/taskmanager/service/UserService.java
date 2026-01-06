package ru.naujava.taskmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
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
     * Находит пользователя по Telegram ID.
     */
    public Optional<User> findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    /**
     * Возвращает существующего пользователя по Telegram ID или создает
     * нового, если пользователь не найден.
     * @throws IllegalArgumentException если telegramId null или не положительное число
     */
    public User getOrCreateByTelegramId(Long telegramId) {
        if (telegramId == null) {
            throw new IllegalArgumentException("Telegram ID не может быть null");
        }
        if (telegramId <= 0) {
            throw new IllegalArgumentException("Telegram ID должен быть положительным числом");
        }
        return userRepository.findByTelegramId(telegramId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setTelegramId(telegramId);
                    User saved = userRepository.save(newUser);
                    log.info("Создан новый пользователь с telegramId: {}", telegramId);
                    return saved;
                });
    }
}
