package ru.naujava.taskmanager.service;

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
    private final UserRepository userRepository;

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
     */
    public User getOrCreateByTelegramId(Long telegramId) {
        if (telegramId == null) {
            throw new IllegalArgumentException("Telegram ID не может быть null");
        }
        return userRepository.findByTelegramId(telegramId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setTelegramId(telegramId);
                    return userRepository.save(newUser);
                });
    }

    /**
     * Сохраняет или обновляет пользователя.
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Удаляет пользователя по Telegram ID.
     */
    public void deleteByTelegramId(Long telegramId) {
        userRepository.deleteByTelegramId(telegramId);
    }
}
